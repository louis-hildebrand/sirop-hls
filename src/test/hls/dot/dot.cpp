#include "HLS/hls.h"
#include <stdio.h>
#include <stdint.h>

using namespace ihc;

constexpr int N = 840;

template<unsigned SystemID> class InputPipeID {};
template<unsigned SystemID> class OutputPipeID {};

constexpr int A = 0;
constexpr int B = 1;
constexpr int C = 2;
component void dot(
    ihc::pipe<class InputPipeID<A>, uint16_t> &a,
    ihc::pipe<class InputPipeID<B>, uint16_t> &b,
    ihc::pipe<class OutputPipeID<C>, uint16_t> &c
) {
    uint16_t sum = 0;
    for (int i = 0; i < N; i++) {
        sum += a.read() * b.read();
    }
    c.write(sum);
}

int main() {
    uint16_t a_arr[N];
    for (int i = 0; i < N; i++) {
        a_arr[i] = i % 16;
    }
    uint16_t b_arr[N];
    for (int i = 0; i < N; i++) {
        b_arr[i] = (N - 1 - i) % 16;
    }

    ihc::pipe<class InputPipeID<A>, uint16_t> a_stm;
    ihc::pipe<class InputPipeID<B>, uint16_t> b_stm;
    ihc::pipe<class OutputPipeID<C>, uint16_t> c_stm;

    for (unsigned int i = 0; i < N; i++) {
        a_stm.write(a_arr[i]);
        b_stm.write(b_arr[i]);
    }

    dot(a_stm, b_stm, c_stm);

    uint16_t result = c_stm.read();

    uint16_t expected = 0;
    for (int i = 0; i < N; i++) {
        expected += a_arr[i] * b_arr[i];
    }

    bool pass = true;
    if (result != expected) {
        printf("ERROR: Expected %u, found %u\n", expected, result);
        pass = false;
    }

    if (pass) {
        printf("PASSED\n");
    }
    else {
        printf("FAILED\n");
    }

    return 0;

}
