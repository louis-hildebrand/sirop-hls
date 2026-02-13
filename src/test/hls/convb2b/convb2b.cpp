#include "HLS/hls.h"
#include <stdio.h>
#include <stdint.h>

using namespace ihc;

constexpr int WIDTH = 1920;
constexpr int HEIGHT = 1080;
constexpr uint32_t KERNEL3x3[3][3] = {
    {1, 2, 1},
    {2, 4, 2},
    {1, 2, 1},
};
constexpr uint32_t KERNEL2x2[2][2] = {
    {1, 2},
    {4, 1},
};

constexpr int A = 0;
constexpr int B = 1;
constexpr int C = 2;
template<unsigned SystemID> class PipeID {};
ihc::pipe<PipeID<A>, uint32_t> pipe_a;
ihc::stream<uint32_t> pipe_b;
ihc::pipe<PipeID<C>, uint32_t> pipe_c;

template<unsigned int img_width, unsigned int win_width, unsigned int win_height>
class LineBuffer2D {
public:
    uint32_t big_buffer[win_height-1][img_width];
    uint32_t small_buffer[win_width];

public:
    void shift(uint32_t next) {
        #pragma unroll
        for (int i = 0; i < win_height-1; i++) {
            #pragma unroll
            for (int j = 1; j < img_width; j++) {
                big_buffer[i][j-1] = big_buffer[i][j];
            }
            if (i + 1 < win_height) {
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

void conv3x3() {
    LineBuffer2D<WIDTH, 3, 3> buffer;
    for (int i = 0; i < HEIGHT; i++) {
        for (int j = 0; j < WIDTH; j++) {
            buffer.shift(pipe_a.read());
            if (i >= 2 && j >= 2) {
                uint32_t window[3][3] = {
                    buffer.big_buffer[0][0], buffer.big_buffer[0][1], buffer.big_buffer[0][2],
                    buffer.big_buffer[1][0], buffer.big_buffer[1][1], buffer.big_buffer[1][2],
                    buffer.small_buffer[0], buffer.small_buffer[1], buffer.small_buffer[2]
                };
                uint32_t sum = 0;
                #pragma unroll
                for (int i = 0; i < 3; i++) {
                    #pragma unroll
                    for (int j = 0; j < 3; j++) {
                        sum += KERNEL3x3[i][j] * window[i][j];
                    }
                }
                pipe_b.write(sum);
            }
        }
    }
}

void conv2x2() {
    LineBuffer2D<WIDTH-2, 2, 2> buffer;
    for (int i = 0; i < HEIGHT-2; i++) {
        for (int j = 0; j < WIDTH-2; j++) {
            buffer.shift(pipe_b.read());
            if (i >= 1 && j >= 1) {
                uint32_t window[2][2] = {
                    buffer.big_buffer[0][0], buffer.big_buffer[0][1],
                    buffer.small_buffer[0], buffer.small_buffer[1],
                };
                uint32_t sum = 0;
                #pragma unroll
                for (int i = 0; i < 2; i++) {
                    #pragma unroll
                    for (int j = 0; j < 2; j++) {
                        sum += KERNEL2x2[i][j] * window[i][j];
                    }
                }
                pipe_c.write(sum);
            }
        }
    }
}

component void convb2b() {
    ihc::launch<conv3x3>();
    ihc::launch<conv2x2>();
    ihc::collect<conv3x3>();
    ihc::collect<conv2x2>();
}

int main() {
    printf("Filling input array...\n");
    uint32_t in_arr[HEIGHT][WIDTH];
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

    convb2b();

    printf("Computing expected outputs...\n");
    uint32_t expected_intermediate[HEIGHT-2][WIDTH-2] = {};
    for (int i = 0; i < HEIGHT-2; i++) {
        for (int j = 0; j < WIDTH-2; j++) {
            for (int di = 0; di < 3; di++) {
                for (int dj = 0; dj < 3; dj++) {
                    expected_intermediate[i][j] += KERNEL3x3[di][dj] * in_arr[i+di][j+dj];
                }
            }
        }
    }
    uint32_t expected[HEIGHT-3][WIDTH-3] = {};
    for (int i = 0; i < HEIGHT-3; i++) {
        for (int j = 0; j < WIDTH-3; j++) {
            for (int di = 0; di < 2; di++) {
                for (int dj = 0; dj < 2; dj++) {
                    expected[i][j] += KERNEL2x2[di][dj] * expected_intermediate[i+di][j+dj];
                }
            }
        }
    }

    printf("Comparing actual and expected outputs...\n");
    bool pass = true;
    for (int i = 0; i < HEIGHT-3; i++) {
        for (int j = 0; j < WIDTH-3; j++) {
            uint32_t result = pipe_c.read();
            if (result != expected[i][j]) {
                printf("ERROR(%u,%u): Expected %u, found %u\n", i, j, expected[i][j], result);
                pass = false;
            } else {
                printf("OK(%u,%u)\n", i, j);
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
