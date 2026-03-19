#include "HLS/ac_int.h"
#include "HLS/hls.h"
#include "HLS/stdio.h"

component uint8 add5(uint8 x) {
    return x + 5;
}

int main() {
    constexpr int SIZE = 200;

    uint8 input[SIZE];
    for (uint8 i = 0; i < SIZE; i++) {
        input[i] = i;
    }

    uint8 result[SIZE];
    for (int i = 0; i < SIZE; i++) {
        ihc_hls_enqueue(&result[i], &add5, input[i]);
    }

    ihc_hls_component_run_all(add5);

    uint8 expected[SIZE];
    for (uint8 i = 0; i < SIZE; i++) {
        expected[i] = i + 5;
    }

    bool pass = true;
    for (int i = 0; i < SIZE; i++) {
        if (result[i] != expected[i]) {
            printf("ERROR: Expected %lu, found %lu\n", (unsigned long)expected[i], (unsigned long)result[i]);
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
