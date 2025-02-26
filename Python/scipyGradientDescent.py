import numpy as np
import scipy.optimize
import matplotlib.pyplot as plt
import pandas as pd


def makeRandomInputPoints(function: callable, range: tuple) -> tuple:
    xData = np.linspace(range[0], range[1], 1000)
    yData = function(xData) + 0.1 * np.random.normal(size=xData.size)
    return xData, yData


def getDataPoints(filePath: str) -> ():
    data = np.transpose(pd.read_excel(filePath).values)
    yDataTitle = data[0][1]
    xDataTitles = [inner[0] for inner in data]
    yDataTuple = (yDataTitle, data[0][2:])
    xDataTuples = [(xDataTitles[i], data[i][2:]) for i in range(1, len(xDataTitles))]
    return yDataTuple, xDataTuples


def graphAll(coefficients: [], function: callable, xValues: []):
    xIterator = np.linspace(min(xValues), max(xValues), 1000)
    functionYValues = function(xIterator, *coefficients)
    plt.plot(xIterator, functionYValues, label='curve fit')


def f(x, A=1, B=1, C=1):
    return A / (1 + np.exp(-B * (x - C)))


def getXYValues(yData, xData, delimiter):
    yValues = [yData[i] for i in range(len(yData)) if xData[i] != delimiter]
    xValues = [xData[i] for i in range(len(xData)) if xData[i] != delimiter]
    return yValues, xValues


def plotData(yData: [], xData: [], xLabel: str):
    plt.scatter(yData, xData, label=xLabel)


def plotMultipleDatas(yData: [], xDatas: [()]):
    for xData in xDatas:
        plt.scatter(yData, xData[1], label=xData[0])


def main():
    # range = (-20, 20)
    # xData, yData = makeInputPoints(f, range)
    # coefficients = scipy.optimize.curve_fit(f, xData, yData)[0]
    # graphAll(xData, yData, coefficients, f, range)

    yColumn, xColumns = getDataPoints("TCP23_data_vetted.xlsx")
    yData = yColumn[1]
    xDataList = xColumns[1]
    xLabel = xDataList[0]
    yValues, xValues = getXYValues(yData, xDataList[1], '--')
    ax = plt.gca()
    ax.set_ylim([0, 4000])
    ax.set_xlim([min(yData) - 1, max(yData) + 1])
    plotData(yValues, xValues, xLabel)

    initial_guesses = [1, 1, 1]
    bounds = (0, [np.inf, np.inf, np.inf])
    coefficients, _ = scipy.optimize.curve_fit(f, xValues, yValues, p0=initial_guesses)
    graphAll(coefficients, f, xValues)

    plt.legend()
    plt.show()
    pass


if __name__ == '__main__':
    main()