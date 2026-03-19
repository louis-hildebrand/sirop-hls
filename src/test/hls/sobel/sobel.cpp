#include <cmath>
#include "HLS/ac_int.h"
#include "HLS/hls.h"
#include "HLS/stdio.h"

constexpr int WIDTH = 1920;
constexpr int HEIGHT = 1080;
constexpr int32 KERNEL_X[3][3] = {
    {-1, 0, 1},
    {-2, 0, 2},
    {-1, 0, 1},
};
constexpr int32 KERNEL_Y[3][3] = {
    {-1, -2, -1},
    { 0,  0,  0},
    { 1,  2,  1},
};

template<unsigned SystemID> class PipeID {};
ihc::pipe<PipeID<0>, int32> pipe_in;
ihc::pipe<PipeID<1>, int32> pipe_out;

template<unsigned int img_width, unsigned int win_width, unsigned int win_height>
class LineBuffer2D {
public:
    int32 big_buffer[win_height-1][img_width];
    int32 small_buffer[win_width];

public:
    void shift(int32 next) {
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

component void sobel() {
    LineBuffer2D<WIDTH, 3, 3> buffer;
    for (int i = 0; i < HEIGHT; i++) {
        for (int j = 0; j < WIDTH; j++) {
            buffer.shift(pipe_in.read());
            if (i >= 2 && j >= 2) {
                int32 window[3][3] = {
                    buffer.big_buffer[0][0], buffer.big_buffer[0][1], buffer.big_buffer[0][2],
                    buffer.big_buffer[1][0], buffer.big_buffer[1][1], buffer.big_buffer[1][2],
                    buffer.small_buffer[0], buffer.small_buffer[1], buffer.small_buffer[2]
                };
                int32 gx = 0;
                #pragma unroll
                for (int i = 0; i < 3; i++) {
                    #pragma unroll
                    for (int j = 0; j < 3; j++) {
                        gx += KERNEL_X[i][j] * window[i][j];
                    }
                }
                int32 gy = 0;
                #pragma unroll
                for (int i = 0; i < 3; i++) {
                    #pragma unroll
                    for (int j = 0; j < 3; j++) {
                        gy += KERNEL_Y[i][j] * window[i][j];
                    }
                }
                int32 norm_squared = gx*gx + gy*gy;
                int32 lo = 0;
                int32 hi = 65535;
                #pragma unroll
                for (int _ = 0; _ < 16; _++) {
                    int32 mid = (lo + hi + 1) >> 1;
                    int32 mid_squared = mid*mid;
                    if (mid_squared <= norm_squared) {
                        lo = mid;
                    } else /* mid*mid > norm_squared */ {
                        hi = mid - 1;
                    }
                }
                pipe_out.write(lo);
            }
        }
    }
}

int main() {
    printf("Filling input array...\n");
    int32 in_arr[HEIGHT][WIDTH];
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
            pipe_in.write(in_arr[i][j]);
        }
    }

    sobel();

    printf("Computing expected outputs...\n");
    int32 gx[HEIGHT-2][WIDTH-2] = {};
    for (int i = 0; i < HEIGHT-2; i++) {
        for (int j = 0; j < WIDTH-2; j++) {
            for (int di = 0; di < 3; di++) {
                for (int dj = 0; dj < 3; dj++) {
                    gx[i][j] += KERNEL_X[di][dj] * in_arr[i+di][j+dj];
                }
            }
        }
    }
    int32 gy[HEIGHT-2][WIDTH-2] = {};
    for (int i = 0; i < HEIGHT-2; i++) {
        for (int j = 0; j < WIDTH-2; j++) {
            for (int di = 0; di < 3; di++) {
                for (int dj = 0; dj < 3; dj++) {
                    gy[i][j] += KERNEL_Y[di][dj] * in_arr[i+di][j+dj];
                }
            }
        }
    }
    int32 expected[HEIGHT-2][WIDTH-2] = {};
    for (int i = 0; i < HEIGHT-2; i++) {
        for (int j = 0; j < WIDTH-2; j++) {
            long double x = (long double)(gx[i][j]);
            long double y = (long double)(gy[i][j]);
            long double norm_squared = x*x + y*y;
            long norm = (long)sqrtl(norm_squared);
            expected[i][j] = (int32)norm;
        }
    }

    printf("Comparing actual and expected outputs...\n");
    bool pass = true;
    for (int i = 0; i < HEIGHT-2; i++) {
        for (int j = 0; j < WIDTH-2; j++) {
            int32 result = pipe_out.read();
            if (result != expected[i][j]) {
                printf("ERROR(%d,%d): Expected %lu, found %lu\n", i, j, (unsigned long)expected[i][j], (unsigned long)result);
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
