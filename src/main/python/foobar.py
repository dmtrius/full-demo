def foobar(n):
    for i in range(1, n + 1):
        divisible_by_3 = (i % 3 == 0)
        divisible_by_5 = (i % 5 == 0)

        if divisible_by_3 and divisible_by_5:
            print("Foobar")
        elif divisible_by_3:
            print("Foo")
        elif divisible_by_5:
            print("Bar")
        else:
            print(i)

if __name__ == "__main__":
    ns = 100
    foobar(ns)
