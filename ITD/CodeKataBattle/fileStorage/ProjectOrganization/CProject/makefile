# Variabile che contiene il compilatore da usare
CXX = g++

# Variabile che contiene le opzioni del compilatore
CXXFLAGS = -Wall -g

# Nome del file eseguibile
TARGET = main

# Lista dei file sorgente
SRCS = src/main/c/main.c

# Regola predefinita eseguita da 'make'
all: $(TARGET)

# Regola per costruire l'eseguibile
$(TARGET): $(SRCS)
	$(CXX) $(CXXFLAGS) -o $(TARGET) $(SRCS)

# Regola per pulire i file generati
clean:
	rm -f $(TARGET)