#include "HLS/hls.h"
#include <stdio.h>
#include <stdint.h>

constexpr int WIDTH = 128;
constexpr int HEIGHT = 1024;

template<unsigned SystemID> class PipeID {};
ihc::pipe<class PipeID<0>, uint8_t> pipe_in;
ihc::pipe<class PipeID<1>, uint8_t> pipe_out;

template<typename T, unsigned int img_width, unsigned int win_width, unsigned int win_height>
class LineBuffer2D {
public:
    T big_buffer[win_height-1][img_width];
    T small_buffer[win_width];

public:
    void shift(T next) {
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

component void jacobi() {
    LineBuffer2D<uint8_t, WIDTH, 3, 3> buffer;
    for (int i = 0; i < HEIGHT; i++) {
        for (int j = 0; j < WIDTH; j++) {
            buffer.shift(pipe_in.read());
            if (i >= 2 && j >= 2) {
                uint16_t up = buffer.big_buffer[0][1];
                uint16_t left = buffer.big_buffer[1][0];
                uint16_t right = buffer.big_buffer[1][2];
                uint16_t down = buffer.small_buffer[1];
                uint8_t out = (uint8_t) ((up + left + right + down) >> 2);
                pipe_out.write(out);
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
            pipe_in.write( (uint8_t)in_arr[i][j] );
        }
    }

    jacobi();

    printf("Computing expected outputs...\n");
    uint32_t expected[HEIGHT-2][WIDTH-2];
    for (int i = 1; i < HEIGHT-1; i++) {
        for (int j = 1; j < WIDTH-1; j++) {
            expected[i-1][j-1] = (
                  in_arr[i-1][j]
                + in_arr[i][j-1]
                + in_arr[i][j+1]
                + in_arr[i+1][j]
            ) / 4;
        }
    }

    printf("Comparing actual and expected outputs...\n");
    bool pass = true;
    for (int i = 0; i < HEIGHT-2; i++) {
        for (int j = 0; j < WIDTH-2; j++) {
            uint32_t result = (uint32_t)pipe_out.read();
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
