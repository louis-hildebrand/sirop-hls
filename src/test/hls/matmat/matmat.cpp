#include "HLS/hls.h"
#include <stdio.h>
#include <stdint.h>

using namespace ihc;

constexpr int N = 256;
/* Degree of spatial parallelism.
 * It must cleanly divide N.
 */
constexpr int PAR = 16;

template<unsigned SystemID> class InputPipeID {};
template<unsigned SystemID> class OutputPipeID {};

class Chunk {
public:
    uint16_t values[PAR];
};

uint16_t dot(Chunk a, Chunk b) {
    uint16_t sum = 0;
    #pragma unroll
    for (int i = 0; i < PAR; i++) {
        sum += a.values[i] * b.values[i];
    }
    return sum;
}

constexpr int A = 0;
constexpr int B = 1;
constexpr int C = 2;
component void matmat(
    ihc::pipe<class InputPipeID<A>, Chunk> &a,
    ihc::pipe<class InputPipeID<B>, Chunk> &b_t,
    ihc::pipe<class OutputPipeID<C>, uint16_t> &c
) {
    Chunk b_t_arr[N][N/PAR];
    for (int i = 0; i < N; i++) {
        Chunk a_row[N/PAR];
        for (int j = 0; j < N; j++) {
            uint16_t sum = 0;
            for (int k = 0; k < N/PAR; k++) {
                Chunk a_chunk;
                if (j == 0) {
                    a_chunk = a.read();
                    a_row[k] = a_chunk;
                } else {
                    a_chunk = a_row[k];
                }
                Chunk b_chunk;
                if (i == 0) {
                    b_chunk = b_t.read();
                    b_t_arr[j][k] = b_chunk;
                } else {
                    b_chunk = b_t_arr[j][k];
                }
                sum += dot(a_chunk, b_chunk);
            }
            c.write(sum);
        }
    }
}

int main() {
    uint16_t a_arr[N][N];
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            a_arr[i][j] = ( (4*i+j) * (4*i+j) ) % 6;
        }
    }
    uint16_t b_arr[N][N];
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            b_arr[i][j] = (4*i + j) % 6;
        }
    }

    ihc::pipe<class InputPipeID<A>, Chunk> a_stm;
    ihc::pipe<class InputPipeID<B>, Chunk> b_t_stm;
    ihc::pipe<class OutputPipeID<C>, uint16_t> c_stm;

    printf("Passing inputs for matrix A...\n");
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j += PAR) {
            Chunk chunk;
            for (int k = 0; k < PAR; k++) {
                chunk.values[k] = a_arr[i][j + k];
            }
            a_stm.write(chunk);
        }
    }
    printf("Passing inputs for matrix B...\n");
    for (int j = 0; j < N; j++) {
        for (int i = 0; i < N; i += PAR) {
            Chunk chunk;
            for (int k = 0; k < PAR; k++) {
                chunk.values[k] = b_arr[i + k][j];
            }
            b_t_stm.write(chunk);
        }
    }

    matmat(a_stm, b_t_stm, c_stm);

    printf("Computing expected output...\n");
    uint16_t expected[N][N] = {};
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
                expected[i][j] += a_arr[i][k] * b_arr[k][j];
            }
        }
    }

    printf("Comparing expected and actual outputs...\n");
    bool pass = true;
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            uint16_t result = c_stm.read();
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
