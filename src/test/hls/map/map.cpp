#include "HLS/hls.h"
#include <stdio.h>
#include <stdint.h>

using namespace ihc;

component uint8_t add5(uint8_t x) {
    return x + 5;
}

int main() {
    constexpr int SIZE = 200;

    uint8_t input[SIZE];
    for (uint8_t i = 0; i < SIZE; i++) {
        input[i] = i;
    }

    uint8_t result[SIZE];
    for (unsigned int i = 0; i < SIZE; i++) {
        ihc_hls_enqueue(&result[i], &add5, input[i]);
    }

    ihc_hls_component_run_all(add5);

    uint8_t expected[SIZE];
    for (uint8_t i = 0; i < SIZE; i++) {
        expected[i] = i + 5;
    }

    bool pass = true;
    for (unsigned int i = 0; i < SIZE; i++) {
        if (result[i] != expected[i]) {
            printf("ERROR: Expected %u, found %u\n", expected[i], result[i]);
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
