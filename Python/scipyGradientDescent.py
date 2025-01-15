import numpy as np
import scipy.optimize
import matplotlib.pyplot as plt


def makeInputPoints(function: callable, range) -> tuple:
    xData = np.linspace(range[0], range[1], 1000)
    yData = function(xData) + 0.1 * np.random.normal(size=xData.size)
    return xData, yData


def graphAll(xData, yData, coefficients, function, range):
    plt.plot(xData, yData, 'ro', label='points')
    xData = np.linspace(range[0], range[1], 1000)
    yData = function(xData, *coefficients)
    plt.plot(xData, yData, label='curve fit')
    plt.legend()
    plt.show()


def f(x, A=1, B=1, C=1):
    return A / (1 + np.exp(-B * (x - C)))


def main():
    range = (-20, 20)
    xData, yData = makeInputPoints(f, range)
    coefficients = scipy.optimize.curve_fit(f, xData, yData)[0]
    graphAll(xData, yData, coefficients, f, range)



if __name__ == '__main__':
    main()