import numpy as np
import scipy
from scipy.optimize import curve_fit
import matplotlib.pyplot as plt
import pandas as pd
from scipy.special import expit


def makeRandomInputPoints(function: callable, range: tuple) -> tuple:
    xData = np.linspace(range[0], range[1], 1000)
    yData = function(xData) + 0.1 * np.random.normal(size=xData.size)
    return xData, yData


def getDataPoints(filePath: str) -> ():
    data = np.transpose(pd.read_excel(filePath).values)
    xDataTitle = data[0][1]
    yDataTitles = [inner[0] for inner in data]
    xDataTuple = (xDataTitle, data[0][2:])
    yDataTuples = [(yDataTitles[i], data[i][2:]) for i in range(1, len(yDataTitles))]
    return xDataTuple, yDataTuples


def graphCurveFit(coefficients: [], function: callable, xValues: []):
    xData = np.linspace(min(xValues) - 1, max(xValues) + 1, 1000)
    yData = function(xData, *coefficients)
    plt.plot(xData, yData, label="Fitted Curve")


def logistic(x, A=1, B=1, C=1, D=1):
    return A / (1 + np.exp(-B * (x - C))) + D


def polynomial(x, A=1, B=1, C=1):
    return A * x**2 + B * x + C


def exponential(x, A=1, B=1, C=1, D=1):
    return A * np.exp(B * (x - C)) + D


def getXYValues(xData, yData, delimiter):
    xValues = [float(xData[i]) for i in range(len(xData)) if yData[i] != delimiter]
    yValues = [float(yData[i]) for i in range(len(yData)) if yData[i] != delimiter]
    return xValues, yValues


def plotData(xData: [], yData: [], yLabel: str):
    plt.scatter(xData, yData, label=yLabel)


def plotMultipleDatas(xData: [], yDatas: [()]):
    for yData in yDatas:
        plt.scatter(xData, yData[1], label=yData[0])


def main():
    # range = (-20, 20)
    # xData, yData = makeRandomInputPoints(logistic, range)
    # coefficients = scipy.optimize.curve_fit(logistic, xData, yData)[0]
    # plotData(xData, yData, "Random Data")
    # graphAll(coefficients, logistic, xData)

    xColumn, yColumns = getDataPoints("TCP23_data_vetted.xlsx")
    xData = xColumn[1]
    yDataList = yColumns[3]
    dataLabel = yDataList[0]
    xValues, yValues = getXYValues(xData, yDataList[1], '--')
    ax = plt.gca()

    # also will need to change graph bounds based on scaling
    ax.set_ylim([0, 5.0000])
    ax.set_xlim([min(xData) - 1, max(xData) + 1])
    # use if exp overflow error
    # yValues = [y / 10000 for y in yValues]
    plotData(xValues, yValues, dataLabel)

    # also change initial guess based on scaling
    initial_guesses = [1,1,2017,3.5620]
    bounds = (0, [np.inf, np.inf, np.inf, np.inf])
    xValues = [x for x in xValues]
    coefficients, _ = scipy.optimize.curve_fit(logistic, xValues, yValues, p0=initial_guesses, bounds=bounds, method='trf')
    # coefficients, _ = scipy.optimize.curve_fit(logistic, xValues, yValues)

    graphCurveFit(coefficients, logistic, xData)

    plt.legend()
    plt.show()
    pass


if __name__ == '__main__':
    main()