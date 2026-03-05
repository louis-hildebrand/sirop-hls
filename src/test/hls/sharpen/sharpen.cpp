#include "HLS/hls.h"
#include <stdio.h>
#include <stdint.h>

constexpr int WIDTH = 1920;
constexpr int HEIGHT = 1080;
constexpr uint32_t KERNEL[3][3] = {
    {1, 2, 1},
    {2, 4, 2},
    {1, 2, 1},
};

template<unsigned SystemID> class PipeID {};
ihc::pipe<PipeID<0>, uint32_t> pipe_in;
ihc::pipe<PipeID<1>, uint32_t> pipe_out;

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

component void sharpen() {
    LineBuffer2D<WIDTH, 3, 3> buffer;
    for (int i = 0; i < HEIGHT; i++) {
        for (int j = 0; j < WIDTH; j++) {
            buffer.shift(pipe_in.read());
            if (i >= 2 && j >= 2) {
                uint32_t window[3][3] = {
                    buffer.big_buffer[0][0], buffer.big_buffer[0][1], buffer.big_buffer[0][2],
                    buffer.big_buffer[1][0], buffer.big_buffer[1][1], buffer.big_buffer[1][2],
                    buffer.small_buffer[0], buffer.small_buffer[1], buffer.small_buffer[2]
                };
                uint32_t blurred = 0;
                #pragma unroll
                for (int i = 0; i < 3; i++) {
                    #pragma unroll
                    for (int j = 0; j < 3; j++) {
                        blurred += KERNEL[i][j] * window[i][j];
                    }
                }
                blurred >>= 4;
                uint32_t original = buffer.big_buffer[1][1];
                pipe_out.write( original + ((original - blurred) >> 2) );
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

    printf("Sending data to input stream...\n");
    for (int i = 0; i < HEIGHT; i++) {
        for (int j = 0; j < WIDTH; j++) {
            pipe_in.write(in_arr[i][j]);
        }
    }

    sharpen();

    printf("Computing expected outputs...\n");
    uint32_t blurred[HEIGHT-2][WIDTH-2] = {};
    for (int i = 0; i < HEIGHT-2; i++) {
        for (int j = 0; j < WIDTH-2; j++) {
            for (int di = 0; di < 3; di++) {
                for (int dj = 0; dj < 3; dj++) {
                    blurred[i][j] += KERNEL[di][dj] * in_arr[i+di][j+dj];
                }
            }
            blurred[i][j] = blurred[i][j] >> 4;
        }
    }
    uint32_t cropped[HEIGHT-2][WIDTH-2] = {};
    for (int i = 0; i < HEIGHT-2; i++) {
        for (int j = 0; j < WIDTH-2; j++) {
            cropped[i][j] = in_arr[i+1][j+1];
        }
    }
    uint32_t expected[HEIGHT-2][WIDTH-2] = {};
    for (int i = 0; i < HEIGHT-2; i++) {
        for (int j = 0; j < WIDTH-2; j++) {
            expected[i][j] = cropped[i][j] + ((cropped[i][j] - blurred[i][j]) >> 2);
        }
    }

    printf("Comparing actual and expected outputs...\n");
    bool pass = true;
    for (int i = 0; i < HEIGHT-2; i++) {
        for (int j = 0; j < WIDTH-2; j++) {
            uint32_t result = pipe_out.read();
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
