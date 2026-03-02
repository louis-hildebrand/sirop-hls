#include "HLS/hls.h"
#include <stdio.h>
#include <stdint.h>

using namespace std;
using namespace ihc;

#define READ_INPUTS

constexpr int WIDTH = 1920;
constexpr int HEIGHT = 1080;
constexpr uint32_t KERNEL[3][3] = {
    {1, 2, 1},
    {2, 4, 2},
    {1, 2, 1},
};

struct Colour {
    uint32_t red;
    uint32_t green;
    uint32_t blue;
};

template<unsigned SystemID> class PipeID {};
ihc::pipe<PipeID<0>, uint32_t> pipe_in;
ihc::stream<uint32_t> demosaic_red;
ihc::stream<uint32_t> demosaic_green;
ihc::stream<uint32_t> demosaic_blue;
ihc::stream<uint32_t> sharp_red;
ihc::stream<uint32_t> sharp_green;
ihc::stream<uint32_t> sharp_blue;
ihc::pipe<PipeID<1>, struct Colour> pipe_out;

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

void demosaic() {
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

                uint32_t red;
                uint32_t green;
                uint32_t blue;
                if ( (i % 2 != 0) && (j % 2 != 0) ) {
                    /*
                     * +---+---+---+
                     * | G | B | G |
                     * +---+---+---+
                     * | R | G | R |
                     * +---+---+---+
                     * | G | B | G |
                     * +---+---+---+
                     */
                    red   = (window[1][0] + window[1][2]) >> 1;
                    green = window[1][1];
                    blue  = (window[0][1] + window[2][1]) >> 1;
                } else if ( (i % 2 != 0) && (j % 2 == 0) ) {
                    /*
                     * +---+---+---+
                     * | B | G | B |
                     * +---+---+---+
                     * | G | R | G |
                     * +---+---+---+
                     * | B | G | B |
                     * +---+---+---+
                     */
                    red   = window[1][1];
                    green = (window[0][1] + window[1][0] + window[1][2] + window[2][1]) >> 2;
                    blue  = (window[0][0] + window[0][2] + window[2][0] + window[2][2]) >> 2;
                } else if ( (i % 2 == 0) && (j % 2 != 0) ) {
                    /*
                     * +---+---+---+
                     * | R | G | R |
                     * +---+---+---+
                     * | G | B | G |
                     * +---+---+---+
                     * | R | G | R |
                     * +---+---+---+
                     */
                    red   = (window[0][0] + window[0][2] + window[2][0] + window[2][2]) >> 2;
                    green = (window[0][1] + window[1][0] + window[1][2] + window[2][1]) >> 2;
                    blue  = window[1][1];
                } else {
                    /*
                     * +---+---+---+
                     * | G | R | G |
                     * +---+---+---+
                     * | B | G | B |
                     * +---+---+---+
                     * | G | R | G |
                     * +---+---+---+
                     */
                    red   = (window[0][1] + window[2][1]) >> 1;
                    green = window[1][1];
                    blue  = (window[1][0] + window[1][2]) >> 1;
                }

                printf("%u,%u,%u\n", red, green, blue);
                demosaic_red.write(red);
                demosaic_green.write(green);
                demosaic_blue.write(blue);
            }
        }
    }
}

void sharpen(ihc::stream<uint32_t> &in, ihc::stream<uint32_t> &out) {
    LineBuffer2D<WIDTH-2, 3, 3> buffer;
    for (int i = 0; i < HEIGHT-2; i++) {
        for (int j = 0; j < WIDTH-2; j++) {
            buffer.shift(in.read());
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
                uint32_t original = buffer.small_buffer[2];
                uint32_t sharp;
                if (blurred < original) {
                    sharp = original + ( (original - blurred) >> 2 );
                } else {
                    sharp = original - ( (blurred - original) >> 2 );
                }
                out.write(sharp);
            }
        }
    }
}

void sharpen_red() {
    sharpen(demosaic_red, sharp_red);
}

void sharpen_green() {
    sharpen(demosaic_green, sharp_green);
}

void sharpen_blue() {
    sharpen(demosaic_blue, sharp_blue);
}

void join() {
    for (int i = 0; i < HEIGHT-4; i++) {
        for (int j = 0; j < WIDTH-4; j++) {
            uint32_t red = sharp_red.read();
            uint32_t green = sharp_green.read();
            uint32_t blue = sharp_blue.read();
            struct Colour c = { .red=red, .green=green, .blue=blue };
            pipe_out.write(c);
        }
    }
}

component void camera() {
    ihc::launch<demosaic>();
    ihc::launch<sharpen_red>();
    ihc::launch<sharpen_green>();
    ihc::launch<sharpen_blue>();
    ihc::launch<join>();

    ihc::collect<demosaic>();
    ihc::collect<sharpen_red>();
    ihc::collect<sharpen_green>();
    ihc::collect<sharpen_blue>();
    ihc::collect<join>();
}

int main() {
    printf("Filling input array...\n");
    uint32_t in_arr[HEIGHT][WIDTH];
    for (int i = 0; i < HEIGHT; i++) {
        for (int j = 0; j < WIDTH; j++) {
            in_arr[i][j] = (WIDTH*i + j + 1) * (WIDTH*i + j + 1);
        }
    }

    printf("Sending data to input stream...\n");
    for (int i = 0; i < HEIGHT; i++) {
        for (int j = 0; j < WIDTH; j++) {
            pipe_in.write(in_arr[i][j]);
        }
    }

    camera();

    printf("Loading expected outputs...\n");
    struct Colour expected[HEIGHT-4][WIDTH-4] = {};
    FILE *f = fopen("outputs.txt", "r");
    if (f == NULL) {
        printf("FAILED: could not open file with expected outputs\n");
        return 1;
    }
    char line[1024];
    for (int i = 0; i < HEIGHT-4; i++) {
        for (int j = 0; j < WIDTH-4; j++) {
            fgets(line, 1024, f);
            uint32_t red;
            uint32_t green;
            uint32_t blue;
            sscanf(line, "%u,%u,%u", &red, &green, &blue);
            expected[i][j] = { .red=red, .green=green, .blue=blue };
        }
    }

    printf("Comparing actual and expected outputs...\n");
    bool pass = true;
    for (int i = 0; i < HEIGHT-4; i++) {
        for (int j = 0; j < WIDTH-4; j++) {
            struct Colour result = pipe_out.read();
            bool ok = (
                result.red == expected[i][j].red
                && result.green == expected[i][j].green
                && result.blue == expected[i][j].blue
            );
            if (!ok) {
                printf(
                    "ERROR(%u,%u): Expected (%u,%u,%u), found (%u,%u,%u)\n",
                    i, j,
                    expected[i][j].red, expected[i][j].green, expected[i][j].blue,
                    result.red, result.green, result.blue
                );
                pass = false;
            }
        }
    }

    if (pass) {
        printf("PASSED\n");
        return 0;
    }
    else {
        printf("FAILED\n");
        return 1;
    }

}
