#include "HLS/hls.h"
#include <stdio.h>
#include <stdint.h>

using namespace ihc;

constexpr int WIDTH = 1920;
constexpr int HEIGHT = 1080;
constexpr uint32_t KERNEL[3][3] = {
    {1, 2, 1},
    {2, 4, 2},
    {1, 2, 1},
};

template<unsigned SystemID> class InputPipeID {};
template<unsigned SystemID> class OutputPipeID {};

class LineBuffer2D {
public:
    uint32_t buffer0[WIDTH];
    uint32_t buffer1[WIDTH];
    uint32_t buffer2[3];

public:
    void shift(uint32_t next) {
        #pragma unroll
        for (int i = 1; i < WIDTH; i++) {
            buffer0[i-1] = buffer0[i];
        }
        buffer0[WIDTH-1] = buffer1[0];
        #pragma unroll
        for (int i = 1; i < WIDTH; i++) {
            buffer1[i-1] = buffer1[i];
        }
        buffer1[WIDTH-1] = buffer2[0];
        #pragma unroll
        for (int i = 1; i < 3; i++) {
            buffer2[i-1] = buffer2[i];
        }
        buffer2[2] = next;
    }
};

constexpr int A = 0;
constexpr int B = 1;
component void conv2d(
    ihc::pipe<class InputPipeID<A>, uint32_t> &in,
    ihc::pipe<class OutputPipeID<B>, uint32_t> &out
) {
    LineBuffer2D buffer;
    for (int i = 0; i < HEIGHT; i++) {
        for (int j = 0; j < WIDTH; j++) {
            buffer.shift(in.read());
            if (i >= 2 && j >= 2) {
                uint32_t window[3][3] = {
                    buffer.buffer0[0], buffer.buffer0[1], buffer.buffer0[2],
                    buffer.buffer1[0], buffer.buffer1[1], buffer.buffer1[2],
                    buffer.buffer2[0], buffer.buffer2[1], buffer.buffer2[2]
                };
                uint32_t sum = 0;
                #pragma unroll
                for (int i = 0; i < 3; i++) {
                    #pragma unroll
                    for (int j = 0; j < 3; j++) {
                        sum += KERNEL[i][j] * window[i][j];
                    }
                }
                out.write(sum);
            }
        }
    }
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

    ihc::pipe<class InputPipeID<A>, uint32_t> in_stm;
    ihc::pipe<class OutputPipeID<B>, uint32_t> out_stm;

    printf("Sending data to input stream...\n");
    for (int i = 0; i < HEIGHT; i++) {
        for (int j = 0; j < WIDTH; j++) {
            in_stm.write(in_arr[i][j]);
        }
    }

    conv2d(in_stm, out_stm);

    printf("Computing expected outputs...\n");
    uint32_t expected[HEIGHT-2][WIDTH-2];
    for (int i = 0; i < HEIGHT-2; i++) {
        for (int j = 0; j < WIDTH-2; j++) {
            expected[i][j] = 0;
            for (int di = 0; di < 3; di++) {
                for (int dj = 0; dj < 3; dj++) {
                    expected[i][j] += KERNEL[di][dj] * in_arr[i+di][j+dj];
                }
            }
        }
    }

    printf("Comparing actual and expected outputs...\n");
    bool pass = true;
    for (int i = 0; i < HEIGHT-2; i++) {
        for (int j = 0; j < WIDTH-2; j++) {
            uint32_t result = out_stm.read();
            if (result != expected[i][j]) {
                printf("ERROR: Expected %u, found %u\n", expected[i][j], result);
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
