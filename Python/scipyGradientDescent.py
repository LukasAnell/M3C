import statistics

import numpy as np
import scipy
from scipy.optimize import curve_fit
import matplotlib.pyplot as plt
import pandas as pd
from scipy.special import expit


def makeRandomInputPoints(function: callable, range: tuple) -> tuple:
    # testing function for random input points based on a given function
    xData = np.linspace(range[0], range[1], 1000)
    yData = function(xData) + 0.1 * np.random.normal(size=xData.size)
    return xData, yData


def getDataPoints(filePath: str) -> ():
    # read data from excel file
    data = np.transpose(pd.read_excel(filePath).values)
    # extract x and y data
    xDataTitle = data[0][1]
    yDataTitles = [inner[0] for inner in data]
    # create tuples for x and y data
    # xDataTuple is a tuple with the first element being the x-axis title and the second element being the x-axis data
    # yDataTupleList is a list of tuples with the first element being the y-axis title and the second element being the y-axis data
    xDataTuple = (xDataTitle, data[0][2:])
    yDataTupleList = [(yDataTitles[i], data[i][2:]) for i in range(1, len(yDataTitles))]
    return xDataTuple, yDataTupleList


def getXYValues(xData, yData, delimiter):
    # filter out data points with a delimiter
    xValues = [float(xData[i]) for i in range(len(xData)) if yData[i] != delimiter]
    yValues = [float(yData[i]) for i in range(len(yData)) if yData[i] != delimiter]
    return xValues, yValues


def logistic(x, A=1, B=1, C=1, D=1):
    return A / (1 + np.exp(-B * (x - C))) + D


def polynomial(x, A=1, B=1, C=1):
    return A * x**2 + B * x + C


def exponential(x, A=1, B=1, C=1, D=1):
    return A * np.exp(B * (x - C)) + D


def graphCurveFit(coefficients: [], function: callable, xValues: []):
    # plot fitted curve
    xData = np.linspace(min(xValues) - 1, max(xValues) + 1, 1000)
    yData = function(xData, *coefficients)
    plt.plot(xData, yData, label="Fitted Curve")


def plotMultipleDatas(xData: [], yDatas: [()]):
    # plot data points for multiple data sets
    for yData in yDatas:
        plt.scatter(xData, yData[1], label=yData[0])


def plotAllCountries(xData: [], yDataList: [], fitCurve: bool, function: callable, deScalingFactor: int):
    # change graph limits based on extremes of all data points
    ax = plt.gca()
    yLim = 0
    xLim = 0
    # plot data points for all countries
    for yData in yDataList[1:]:
        xValues, yValues = getXYValues(xData, yData[1], '--')
        xMean, yMean = statistics.mean(xValues), statistics.mean(yValues)
        yValues = [y / deScalingFactor for y in yValues]

        # set graph limits
        yLim = max(yLim, (max(yValues) + yMean) / deScalingFactor)
        xLim = max(xLim, max(xData) + 1)

        scatterData(xValues, yValues, yData[0])
        if fitCurve:
            initialGuesses = [1, 1, xMean, yMean]
            bounds = (0, [np.inf, np.inf, np.inf, np.inf])
            coefficients, _ = scipy.optimize.curve_fit(function, xValues, yValues, p0=initialGuesses, bounds=bounds, method='trf')
            graphCurveFit(coefficients, function, xData)
    ax.set_ylim([0, yLim])
    ax.set_xlim([min(xData) - 1, max(xData) + 1])

def scatterData(xData: [], yData: [], yLabel: str):
    # plot data points
    plt.scatter(xData, yData, label=yLabel)


def plotUnitedStates(xData: [], yData: [], fitCurve: bool, function: callable, deScalingFactor: int, dataLabel: str):
    # plot data points for United States
    xValues, yValues = getXYValues(xData, yData, '--')
    xMean, yMean = statistics.mean(xValues), statistics.mean(yValues)
    yValues = [y / deScalingFactor for y in yValues]

    # set graph limits
    ax = plt.gca()
    ax.set_ylim([0, (max(yValues) + yMean) / deScalingFactor])
    ax.set_xlim([min(xData) - 1, max(xData) + 1])

    scatterData(xValues, yValues, dataLabel)
    if fitCurve:
        initialGuesses = [1, 1, xMean, yMean]
        bounds = (0, [np.inf, np.inf, np.inf, np.inf])
        coefficients, _ = scipy.optimize.curve_fit(function, xValues, yValues, p0=initialGuesses, bounds=bounds, method='trf')
        graphCurveFit(coefficients, function, xData)


def main():

    # obtain data points from .xlsx file
    # xColumn is a tuple with the first element being the x-axis title and the second element being the x-axis data
    # yColumnList is a list of tuples with the first element being the y-axis title and the second element being the y-axis data
    # xValues and yValues are filtered to remove any data points with a delimiter (this indicates that there is no data for that point)
    xColumn, yColumnList = getDataPoints("TCP23_data_vetted.xlsx")
    xData = xColumn[1]
    """
    United States : 0
    Europe : 1
    France : 2
    China : 3
    India : 4
    Japan : 5
    """
    yDataList = yColumnList[0]
    dataLabel = yDataList[0]

    # plot all countries
    plotAllCountries(xData, yColumnList[:3] + yColumnList[4:], False, exponential, 1)
    # plotUnitedStates(xData, yDataList[1], True, exponential, 1, dataLabel)

    """
    xValues, yValues = getXYValues(xData, yDataList[1], '--')
    # obtain mean values of relevant data
    # will be used as to change graph scaling and initial guesses for curve fitting
    xMean, yMean = statistics.mean(xValues), statistics.mean(yValues)

    # use if exp overflow error
    deScalingFactor = 1

    # set graph limits
    ax = plt.gca()
    ax.set_ylim([0, (max(yValues) + yMean) / deScalingFactor])
    ax.set_xlim([min(xData) - 1, max(xData) + 1])

    # scale data by deScalingFactor
    # xValues = [x - 2000 for x in xValues]
    yValues = [y / deScalingFactor for y in yValues]

    # plot data points
    plotData(xValues, yValues, dataLabel)

    # curve fitting
    initialGuesses = [1, 1, xMean, yMean]
    bounds = (0, [np.inf, np.inf, np.inf, np.inf])
    coefficients, _ = scipy.optimize.curve_fit(exponential, xValues, yValues, p0=initialGuesses, bounds=bounds, method='trf')

    # plot fitted curve
    graphCurveFit(coefficients, exponential, xData)
    """

    # display graph with data points and fitted curve
    plt.legend()
    plt.show()


if __name__ == '__main__':
    main()