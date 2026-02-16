#include "HLS/hls.h"
#include <stdio.h>
#include <stdint.h>

using namespace ihc;

constexpr int N = 16;
constexpr int W = 3;
constexpr int8_t KERNEL[W] = {-1, 0, 1};

template<unsigned SystemID> class InputPipeID {};
template<unsigned SystemID> class OutputPipeID {};

constexpr int A = 0;
constexpr int B = 1;
component void conv1d(
    ihc::pipe<class InputPipeID<A>, uint16_t> &in,
    ihc::pipe<class OutputPipeID<B>, uint16_t> &out
) {
    int8_t window[W];
    #pragma unroll
    for (int i = 1; i < W; i++) {
        window[i] = in.read();
    }
    for (int i = 0; i < N-W+1; i++) {
        // Shift window
        #pragma unroll
        for (int j = 1; j < W; j++) {
            window[j-1] = window[j];
        }
        window[W-1] = in.read();
        // Compute convolution
        int8_t sum = 0;
        #pragma unroll
        for (int j = 0; j < W; j++) {
            sum += window[j] * KERNEL[j];
        }
        out.write(sum);
    }
}

int main() {
    int8_t in_arr[N];
    for (int i = 0; i < N; i++) {
        if (i % 8 < 4) {
            in_arr[i] = -42;
        } else {
            in_arr[i] = 42;
        }
    }

    ihc::pipe<class InputPipeID<A>, uint16_t> in_stm;
    ihc::pipe<class OutputPipeID<B>, uint16_t> out_stm;

    for (int i = 0; i < N; i++) {
        in_stm.write(in_arr[i]);
    }

    conv1d(in_stm, out_stm);

    int8_t expected[N-W+1];
    for (int i = 0; i < N-W+1; i++) {
        expected[i] = 0;
        for (int j = 0; j < W; j++) {
            expected[i] += KERNEL[j] * in_arr[i+j];
        }
    }

    bool pass = true;
    for (int i = 0; i < N-W+1; i++) {
        int8_t result = out_stm.read();
        if (result != expected[i]) {
            printf("ERROR: Expected %u, found %u\n", expected[i], result);
            pass = false;
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
