#include "HLS/ac_int.h"
#include "HLS/hls.h"
#include "HLS/stdio.h"

constexpr int N = 840;

template<unsigned SystemID> class PipeID {};

constexpr int A = 0;
constexpr int B = 1;
constexpr int C = 2;
component void dot(
    ihc::pipe<class PipeID<A>, uint16> &a,
    ihc::pipe<class PipeID<B>, uint16> &b,
    ihc::pipe<class PipeID<C>, uint16> &c
) {
    uint16 sum = 0;
    for (int i = 0; i < N; i++) {
        sum += a.read() * b.read();
    }
    c.write(sum);
}

int main() {
    uint16 a_arr[N];
    for (int i = 0; i < N; i++) {
        a_arr[i] = i % 16;
    }
    uint16 b_arr[N];
    for (int i = 0; i < N; i++) {
        b_arr[i] = (N - 1 - i) % 16;
    }

    ihc::pipe<class PipeID<A>, uint16> a_stm;
    ihc::pipe<class PipeID<B>, uint16> b_stm;
    ihc::pipe<class PipeID<C>, uint16> c_stm;

    for (int i = 0; i < N; i++) {
        a_stm.write(a_arr[i]);
        b_stm.write(b_arr[i]);
    }

    dot(a_stm, b_stm, c_stm);

    uint16 result = c_stm.read();

    uint16 expected = 0;
    for (int i = 0; i < N; i++) {
        expected += a_arr[i] * b_arr[i];
    }

    bool pass = true;
    if (result != expected) {
        printf("ERROR: Expected %lu, found %lu\n", (unsigned long)expected, (unsigned long)result);
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
