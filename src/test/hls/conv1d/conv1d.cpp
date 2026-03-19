#include "HLS/ac_int.h"
#include "HLS/hls.h"
#include "HLS/stdio.h"

constexpr int N = 16;
constexpr int W = 3;
constexpr int8 KERNEL[W] = {-1, 0, 1};

template<unsigned SystemID> class PipeID {};

constexpr int A = 0;
constexpr int B = 1;
component void conv1d(
    ihc::pipe<class PipeID<A>, int8> &pipe_in,
    ihc::pipe<class PipeID<B>, int8> &pipe_out
) {
    int8 window[W];
    for (int i = 0; i < N; i++) {
        // Shift window
        #pragma unroll
        for (int j = 1; j < W; j++) {
            window[j-1] = window[j];
        }
        window[W-1] = pipe_in.read();
        // Compute convolution
        int8 sum = 0;
        #pragma unroll
        for (int j = 0; j < W; j++) {
            sum += window[j] * KERNEL[j];
        }
        if (i >= W-1) {
            pipe_out.write(sum);
        }
    }
}

int main() {
    int8 in_arr[N];
    for (int i = 0; i < N; i++) {
        if (i % 8 < 4) {
            in_arr[i] = -42;
        } else {
            in_arr[i] = 42;
        }
    }

    ihc::pipe<class PipeID<A>, int8> in_stm;
    ihc::pipe<class PipeID<B>, int8> out_stm;

    for (int i = 0; i < N; i++) {
        in_stm.write(in_arr[i]);
    }

    conv1d(in_stm, out_stm);

    int8 expected[N-W+1];
    for (int i = 0; i < N-W+1; i++) {
        expected[i] = 0;
        for (int j = 0; j < W; j++) {
            expected[i] += KERNEL[j] * in_arr[i+j];
        }
    }

    bool pass = true;
    for (int i = 0; i < N-W+1; i++) {
        int8 result = out_stm.read();
        if (result != expected[i]) {
            printf("ERROR: Expected %d, found %d\n", (int)expected[i], (int)result);
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
