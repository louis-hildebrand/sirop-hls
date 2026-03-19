#include "HLS/ac_int.h"
#include "HLS/hls.h"
#include "HLS/stdio.h"

constexpr int WIDTH = 1920;
constexpr int HEIGHT = 1080;
constexpr uint32 KERNEL[3][3] = {
    {1, 2, 1},
    {2, 4, 2},
    {1, 2, 1},
};

struct Colour {
    uint32 red;
    uint32 green;
    uint32 blue;
};

template<unsigned SystemID> class PipeID {};
ihc::pipe<PipeID<0>, uint32> pipe_in;
ihc::stream<struct Colour> pipe_demosaic;
ihc::pipe<PipeID<1>, struct Colour> pipe_out;

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

void demosaic() {
    LineBuffer2D<uint32, WIDTH, 3, 3> buffer;
    for (int i = 0; i < HEIGHT; i++) {
        for (int j = 0; j < WIDTH; j++) {
            buffer.shift(pipe_in.read());
            if (i >= 2 && j >= 2) {
                uint32 window[3][3] = {
                    buffer.big_buffer[0][0], buffer.big_buffer[0][1], buffer.big_buffer[0][2],
                    buffer.big_buffer[1][0], buffer.big_buffer[1][1], buffer.big_buffer[1][2],
                    buffer.small_buffer[0], buffer.small_buffer[1], buffer.small_buffer[2]
                };

                uint32 red;
                uint32 green;
                uint32 blue;
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

                struct Colour c = { .red=red, .green=green, .blue=blue };
                pipe_demosaic.write(c);
            }
        }
    }
}

uint32 sharpen_pixel(uint32 window[3][3]) {
    uint32 blurred = 0;
    #pragma unroll
    for (int i = 0; i < 3; i++) {
        #pragma unroll
        for (int j = 0; j < 3; j++) {
            blurred += KERNEL[i][j] * window[i][j];
        }
    }
    blurred >>= 4;
    uint32 original = window[2][2];
    uint32 sharp;
    if (blurred < original) {
        sharp = original + ( (original - blurred) >> 2 );
    } else {
        sharp = original - ( (blurred - original) >> 2 );
    }
    return sharp;
}

void sharpen_rgb() {
    LineBuffer2D<struct Colour, WIDTH-2, 3, 3> buffer;
    for (int i = 0; i < HEIGHT-2; i++) {
        for (int j = 0; j < WIDTH-2; j++) {
            buffer.shift(pipe_demosaic.read());
            if (i >= 2 && j >= 2) {
                struct Colour window[3][3] = {
                    buffer.big_buffer[0][0], buffer.big_buffer[0][1], buffer.big_buffer[0][2],
                    buffer.big_buffer[1][0], buffer.big_buffer[1][1], buffer.big_buffer[1][2],
                    buffer.small_buffer[0], buffer.small_buffer[1], buffer.small_buffer[2]
                };
                uint32 red_window[3][3] = {
                    window[0][0].red, window[0][1].red, window[0][2].red,
                    window[1][0].red, window[1][1].red, window[1][2].red,
                    window[2][0].red, window[2][1].red, window[2][2].red
                };
                uint32 green_window[3][3] = {
                    window[0][0].green, window[0][1].green, window[0][2].green,
                    window[1][0].green, window[1][1].green, window[1][2].green,
                    window[2][0].green, window[2][1].green, window[2][2].green
                };
                uint32 blue_window[3][3] = {
                    window[0][0].blue, window[0][1].blue, window[0][2].blue,
                    window[1][0].blue, window[1][1].blue, window[1][2].blue,
                    window[2][0].blue, window[2][1].blue, window[2][2].blue
                };
                struct Colour sharp = {
                    .red=sharpen_pixel(red_window),
                    .green=sharpen_pixel(green_window),
                    .blue=sharpen_pixel(blue_window)
                };
                pipe_out.write(sharp);
            }
        }
    }
}

component void camera() {
    ihc::launch<demosaic>();
    ihc::launch<sharpen_rgb>();

    ihc::collect<demosaic>();
    ihc::collect<sharpen_rgb>();
}

int main() {
    printf("Filling input array...\n");
    uint32 in_arr[HEIGHT][WIDTH];
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
            unsigned int red;
            unsigned int green;
            unsigned int blue;
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
                    "ERROR(%d,%d): Expected (%lu,%lu,%lu), found (%lu,%lu,%lu)\n",
                    i, j,
                    (unsigned long)expected[i][j].red, (unsigned long)expected[i][j].green, (unsigned long)expected[i][j].blue,
                    (unsigned long)result.red, (unsigned long)result.green, (unsigned long)result.blue
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
