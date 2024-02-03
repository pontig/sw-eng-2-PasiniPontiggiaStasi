# 'make' builds everything
all: main test

# Define the C++ compiler to use
CXX = g++

# Define any directories containing header files
INCLUDES = -I./project/src/main/cpp -I./project/src/test/cpp

# Define the C++ source files
MAIN_SRC = ./project/src/main/cpp/main.cpp
TEST_SRC = ./project/src/test/cpp/main_test.cpp

# Define the C++ object files
MAIN_OBJ = $(MAIN_SRC:.cpp=.o)
TEST_OBJ = $(TEST_SRC:.cpp=.o)

# The executable file that will be created
MAIN_EXEC = main
TEST_EXEC = test

# 'make main'
main: $(MAIN_OBJ)
	$(CXX) $(INCLUDES) -o $(MAIN_EXEC) $(MAIN_OBJ)

# 'make test'
test: $(TEST_OBJ)
	$(CXX) $(INCLUDES) -o $(TEST_EXEC) $(TEST_OBJ) -lgtest -lgtest_main -pthread

# 'make clean'
clean:
	$(RM) $(MAIN_EXEC) $(TEST_EXEC) *.o *~