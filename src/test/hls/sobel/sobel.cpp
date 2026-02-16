#include "HLS/hls.h"
#include <cmath>
#include <stdio.h>
#include <stdint.h>

using namespace ihc;

constexpr int WIDTH = 1920;
constexpr int HEIGHT = 1080;
constexpr int32_t KERNEL_X[3][3] = {
    {-1, 0, 1},
    {-2, 0, 2},
    {-1, 0, 1},
};
constexpr int32_t KERNEL_Y[3][3] = {
    {-1, -2, -1},
    { 0,  0,  0},
    { 1,  2,  1},
};

template<unsigned SystemID> class PipeID {};
ihc::pipe<PipeID<0>, int32_t> pipe_a;
/* Some buffering is needed to get the throughput where we want it.
 * It's not clear why this is; the two branches look to me like they should have
 * identical latency.
 */
ihc::stream<int32_t, ihc::buffer<4>> pipe_b;
ihc::stream<int32_t, ihc::buffer<4>> pipe_c;
ihc::stream<int32_t> pipe_d;
ihc::stream<int32_t> pipe_e;
ihc::pipe<PipeID<6>, int32_t> pipe_f;

template<unsigned int img_width, unsigned int win_width, unsigned int win_height>
class LineBuffer2D {
public:
    int32_t big_buffer[win_height-1][img_width];
    int32_t small_buffer[win_width];

public:
    void shift(int32_t next) {
        #pragma unroll
        for (int i = 0; i < win_height-1; i++) {
            #pragma unroll
            for (int j = 1; j < img_width; j++) {
                big_buffer[i][j-1] = big_buffer[i][j];
            }
            if (i + 1 < win_height - 1) {
                big_buffer[i][img_width-1] = big_buffer[i+1][0];
            }
        }
        big_buffer[win_height-2][img_width-1] = small_buffer[0];
        #pragma unroll
        for (int i = 1; i < win_width; i++) {
            small_buffer[i-1] = small_buffer[i];
        }
        small_buffer[win_width-1] = next;
    }
};

void fork_img() {
    for (int i = 0; i < HEIGHT; i++) {
        for (int j = 0; j < WIDTH; j++) {
            int32_t x = pipe_a.read();
            pipe_b.write(x);
            pipe_c.write(x);
        }
    }
}

void compute_gx() {
    LineBuffer2D<WIDTH, 3, 3> buffer;
    for (int i = 0; i < HEIGHT; i++) {
        for (int j = 0; j < WIDTH; j++) {
            buffer.shift(pipe_b.read());
            if (i >= 2 && j >= 2) {
                int32_t window[3][3] = {
                    buffer.big_buffer[0][0], buffer.big_buffer[0][1], buffer.big_buffer[0][2],
                    buffer.big_buffer[1][0], buffer.big_buffer[1][1], buffer.big_buffer[1][2],
                    buffer.small_buffer[0], buffer.small_buffer[1], buffer.small_buffer[2]
                };
                int32_t sum = 0;
                #pragma unroll
                for (int i = 0; i < 3; i++) {
                    #pragma unroll
                    for (int j = 0; j < 3; j++) {
                        sum += KERNEL_X[i][j] * window[i][j];
                    }
                }
                pipe_d.write(sum);
            }
        }
    }
}

void compute_gy() {
    LineBuffer2D<WIDTH, 3, 3> buffer;
    for (int i = 0; i < HEIGHT; i++) {
        for (int j = 0; j < WIDTH; j++) {
            buffer.shift(pipe_c.read());
            if (i >= 2 && j >= 2) {
                int32_t window[3][3] = {
                    buffer.big_buffer[0][0], buffer.big_buffer[0][1], buffer.big_buffer[0][2],
                    buffer.big_buffer[1][0], buffer.big_buffer[1][1], buffer.big_buffer[1][2],
                    buffer.small_buffer[0], buffer.small_buffer[1], buffer.small_buffer[2]
                };
                int32_t sum = 0;
                #pragma unroll
                for (int i = 0; i < 3; i++) {
                    #pragma unroll
                    for (int j = 0; j < 3; j++) {
                        sum += KERNEL_Y[i][j] * window[i][j];
                    }
                }
                pipe_e.write(sum);
            }
        }
    }
}

void compute_norm() {
    for (int i = 0; i < HEIGHT-2; i++) {
        for (int j = 0; j < WIDTH-2; j++) {
            int32_t x = pipe_d.read();
            int32_t y = pipe_e.read();
            int32_t norm_squared = x*x + y*y;
            int32_t lo = 0;
            int32_t hi = 65535;
            #pragma unroll
            for (int _ = 0; _ < 16; _++) {
                int32_t mid = (lo + hi + 1) >> 1;
                if (mid*mid <= norm_squared) {
                    lo = mid;
                } else /* mid*mid > norm_squared */ {
                    hi = mid - 1;
                }
            }
            pipe_f.write(lo);
        }
    }
}

component void sobel() {
    ihc::launch<fork_img>();
    ihc::launch<compute_gx>();
    ihc::launch<compute_gy>();
    ihc::launch<compute_norm>();
    ihc::collect<fork_img>();
    ihc::collect<compute_gx>();
    ihc::collect<compute_gy>();
    ihc::collect<compute_norm>();
}

int main() {
    printf("Filling input array...\n");
    int32_t in_arr[HEIGHT][WIDTH];
    for (int i = 0; i < HEIGHT; i++) {
        for (int j = 0; j < WIDTH; j++) {
            if ( (i % 20 < 10) == (j % 20 < 10) ) {
                in_arr[i][j] = 255;
            } else {
                in_arr[i][j] = 0;
            }
        }
    }

    printf("Sending data to input stream...\n");
    for (int i = 0; i < HEIGHT; i++) {
        for (int j = 0; j < WIDTH; j++) {
            pipe_a.write(in_arr[i][j]);
        }
    }

    sobel();

    printf("Computing expected outputs...\n");
    int32_t gx[HEIGHT-2][WIDTH-2] = {};
    for (int i = 0; i < HEIGHT-2; i++) {
        for (int j = 0; j < WIDTH-2; j++) {
            for (int di = 0; di < 3; di++) {
                for (int dj = 0; dj < 3; dj++) {
                    gx[i][j] += KERNEL_X[di][dj] * in_arr[i+di][j+dj];
                }
            }
        }
    }
    int32_t gy[HEIGHT-2][WIDTH-2] = {};
    for (int i = 0; i < HEIGHT-2; i++) {
        for (int j = 0; j < WIDTH-2; j++) {
            for (int di = 0; di < 3; di++) {
                for (int dj = 0; dj < 3; dj++) {
                    gy[i][j] += KERNEL_Y[di][dj] * in_arr[i+di][j+dj];
                }
            }
        }
    }
    int32_t expected[HEIGHT-2][WIDTH-2] = {};
    for (int i = 0; i < HEIGHT-2; i++) {
        for (int j = 0; j < WIDTH-2; j++) {
            int32_t norm_squared = gx[i][j]*gx[i][j] + gy[i][j]*gy[i][j];
            int32_t norm = (int32_t) std::sqrt(norm_squared);
            expected[i][j] = norm;
        }
    }

    printf("Comparing actual and expected outputs...\n");
    bool pass = true;
    for (int i = 0; i < HEIGHT-2; i++) {
        for (int j = 0; j < WIDTH-2; j++) {
            int32_t result = pipe_f.read();
            if (result != expected[i][j]) {
                printf("ERROR(%u,%u): Expected %u, found %u\n", i, j, expected[i][j], result);
                pass = false;
            }
        }
    }

    if (pass) {
        printf("PASSED\n");
    }
    else {
        printf("FAILED\n");
    }

    return 0;

}
