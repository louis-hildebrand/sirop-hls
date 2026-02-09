#include "HLS/hls.h"
#include <stdio.h>
#include <stdint.h>

using namespace ihc;

constexpr int N = 256;

template<unsigned SystemID> class InputPipeID {};
template<unsigned SystemID> class OutputPipeID {};

constexpr int A = 0;
constexpr int B = 1;
constexpr int C = 2;
component void matvec(
    ihc::pipe<class InputPipeID<A>, uint16_t> &mat,
    ihc::pipe<class InputPipeID<B>, uint16_t> &vec,
    ihc::pipe<class OutputPipeID<C>, uint16_t> &out
) {
    uint16_t vec_arr[N];
    for (int i = 0; i < N; i++) {
        vec_arr[i] = vec.read();
    }
    for (int i = 0; i < N; i++) {
        uint16_t sum = 0;
        for (int j = 0; j < N; j++) {
            sum += mat.read() * vec_arr[j];
        }
        out.write(sum);
    }
}

int main() {
    uint16_t mat_arr[N][N];
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            mat_arr[i][j] = (i + j) % 16;
        }
    }
    uint16_t vec_arr[N];
    for (int i = 0; i < N; i++) {
        vec_arr[i] = i % 16;
    }

    ihc::pipe<class InputPipeID<A>, uint16_t> mat_stm;
    ihc::pipe<class InputPipeID<B>, uint16_t> vec_stm;
    ihc::pipe<class OutputPipeID<C>, uint16_t> out_stm;

    for (int i = 0; i < N; i++) {
        for (unsigned int j = 0; j < N; j++) {
            mat_stm.write(mat_arr[i][j]);
        }
    }
    for (int i = 0; i < N; i++) {
        vec_stm.write(vec_arr[i]);
    }

    matvec(mat_stm, vec_stm, out_stm);

    uint16_t expected[N];
    for (int i = 0; i < N; i++) {
        expected[i] = 0;
        for (int j = 0; j < N; j++) {
            expected[i] += mat_arr[i][j] * vec_arr[j];
        }
    }

    bool pass = true;
    for (int i = 0; i < N; i++) {
        uint16_t result = out_stm.read();
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
