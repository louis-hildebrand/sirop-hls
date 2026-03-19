#include "HLS/ac_int.h"
#include "HLS/hls.h"
#include "HLS/stdio.h"

constexpr int N = 256;

template<unsigned SystemID> class PipeID {};

ihc::pipe<class PipeID<0>, uint16> mat;
ihc::pipe<class PipeID<1>, uint16> vec;
ihc::pipe<class PipeID<2>, uint16> out;

component void matvec() {
    uint16 vec_arr[N];
    for (int i = 0; i < N; i++) {
        uint16 sum = 0;
        for (int j = 0; j < N; j++) {
            uint16 v;
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
    uint16 mat_arr[N][N];
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            mat_arr[i][j] = (i + j) % 16;
        }
    }
    uint16 vec_arr[N];
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

    uint16 expected[N];
    for (int i = 0; i < N; i++) {
        expected[i] = 0;
        for (int j = 0; j < N; j++) {
            expected[i] += mat_arr[i][j] * vec_arr[j];
        }
    }

    bool pass = true;
    for (int i = 0; i < N; i++) {
        uint16 result = out.read();
        if (result != expected[i]) {
            printf("ERROR: Expected %lu, found %lu\n", (unsigned long)expected[i], (unsigned long)result);
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
