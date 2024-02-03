import unittest
from src.main.python.main import somma_interi, somma_float

class TestSomma(unittest.TestCase):

    def test_somma_interi_semplice(self):
        """
        Test per la somma di due numeri interi
        """
        risultato = somma_interi(2, 3)
        self.assertEqual(risultato, 5)

    def test_somma_interi_zero(self):
        """
        Test per la somma di un numero e zero
        """
        risultato = somma_interi(4, 0)
        self.assertEqual(risultato, 4)

    def test_somma_interi_negativi(self):
        """
        Test per la somma di due numeri negativi
        """
        risultato = somma_interi(-1, -2)
        self.assertEqual(risultato, -3)

    def test_somma_float_semplice(self):
        """
        Test per la somma di due numeri float
        """
        risultato = somma_float(2.5, 3.14)
        self.assertEqual(risultato, 5.64)

    def test_somma_float_zero(self):
        """
        Test per la somma di un numero e zero
        """
        risultato = somma_float(4.5, 0.0)
        self.assertEqual(risultato, 4.5)

    def test_somma_float_negativi(self):
        """
        Test per la somma di due numeri negativi
        """
        risultato = somma_float(-1.5, -2.2)
        self.assertEqual(risultato, -3.7)

if __name__ == '__main__':
    unittest.main()
