#include "HLS/hls.h"
#include <stdint.h>
#include <stdio.h>

constexpr int N = 256;

template<unsigned SystemID> class PipeID {};

ihc::pipe<class PipeID<0>, uint16_t> mat;
ihc::pipe<class PipeID<1>, uint16_t> vec;
ihc::pipe<class PipeID<2>, uint16_t> out;

component void matvec() {
    uint16_t vec_arr[N];
    for (int i = 0; i < N; i++) {
        uint16_t sum = 0;
        for (int j = 0; j < N; j++) {
            uint16_t v;
            if (i == 0) {
                v = vec.read();
                vec_arr[j] = v;
            } else {
                v = vec_arr[j];
            }
            sum += mat.read() * v;
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

    for (int i = 0; i < N; i++) {
        for (unsigned int j = 0; j < N; j++) {
            mat.write(mat_arr[i][j]);
        }
    }
    for (int i = 0; i < N; i++) {
        vec.write(vec_arr[i]);
    }

    matvec();

    uint16_t expected[N];
    for (int i = 0; i < N; i++) {
        expected[i] = 0;
        for (int j = 0; j < N; j++) {
            expected[i] += mat_arr[i][j] * vec_arr[j];
        }
    }

    bool pass = true;
    for (int i = 0; i < N; i++) {
        uint16_t result = out.read();
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
