#include "gtest/gtest.h"
#include <iostream>
#include <sstream>

// Function prototype for main
int main();

TEST(MainTest, OutputsHelloWorld) {
    // Redirect std::cout
    std::streambuf* oldCoutStreamBuf = std::cout.rdbuf();
    std::ostringstream strCout;
    std::cout.rdbuf(strCout.rdbuf());

    // Call main
    main();

    // Restore old cout.
    std::cout.rdbuf(oldCoutStreamBuf);

    // Compare
    ASSERT_EQ(strCout.str(), "Hello, World in C++!\n");
}

int main(int argc, char **argv) {
    ::testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}
