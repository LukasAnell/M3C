import statistics
import numpy as np
import scipy
from scipy.optimize import curve_fit
import matplotlib.pyplot as plt
import pandas as pd


def makeRandomInputPoints(function: callable, dataRange: tuple) -> tuple:
    # testing function for random input points based on a given function
    xData = np.linspace(dataRange[0], dataRange[1], 1000)
    yData = function(xData) + 0.1 * np.random.normal(size=xData.size)
    return xData, yData


def getDataPointsCSV(filePath: str) -> tuple:
    # read data from CSV file
    data = np.transpose(pd.read_csv(filePath).values)
    # extract x and y data
    xData = data[0]
    yData = data[1]
    return xData, yData


def getDataPoints(filePath: str) -> tuple:
    # read data from Excel file
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


def getXYValues(xData: [], yData: [], delimiter: str) -> tuple:
    # filter out data points with a delimiter
    xValues = [float(xData[i]) for i in range(len(xData)) if yData[i] != delimiter]
    yValues = [float(yData[i]) for i in range(len(yData)) if yData[i] != delimiter]
    return xValues, yValues


def biLinear(x: float, y: float, A: float = 1, B: float = 1, C: float = 1) -> float:
    return A * x + B * y + C


def logistic(x: float, A: float = 1, B: float = 1, C: float = 1, D: float = 1) -> float:
    return A / (1 + np.exp(-B * (x - C))) + D


def polynomial(x: float, A: float = 1, B: float = 1, C: float = 1) -> float:
    return A * x**2 + B * x + C


def exponential(x: float, A: float = 1, B: float = 1, C: float = 1, D: float = 1) -> float:
    return A * np.exp(B * (x - C)) + D


def linear(x: float, A: float = 1, B: float = 1, C: float = 1) -> float:
    return A * (x - B) + C


def sinusoidal(x: float, A: float = 1, B: float = 1, C: float = 1, D: float = 1) -> float:
    return A * np.sin(B * (x - C)) + D


def graphCurveFit(coefficients: [float], function: callable, xValues: []) -> None:
    # plot fitted curve
    xData = np.linspace(min(xValues) - 1, max(xValues) + 1, 1000)
    yData = function(xData, *coefficients)
    plt.plot(xData, yData, label="Fitted Curve")


def plotMultipleDatas(xData: [], yDatas: [tuple]) -> None:
    # plot data points for multiple data sets
    for yData in yDatas:
        plt.scatter(xData, yData[1], label=yData[0])


def scatterData(xData: [], yData: [], yLabel: str) -> None:
    # scatter data points
    plt.scatter(xData, yData, label=yLabel)


def plotAllCountries(
        xData: [],
        yDataList: [],
        fitCurve: bool,
        includeErrorBars: bool,
        function: callable,
        deScalingFactor: int,
        delimiter: str
) -> None:
    # change graph limits based on extremes of all data points
    ax = plt.gca()
    yLim = 0
    xLim = 0
    # plot data points for all countries
    for yData in yDataList:
        xValues, yValues = getXYValues(xData, yData[1], delimiter)
        xMean, yMean = statistics.mean(xValues), statistics.mean(yValues)
        yValues = [y / deScalingFactor for y in yValues]

        # set graph limits
        yLim = max(yLim, (max(yValues) + yMean) / deScalingFactor)
        xLim = max(xLim, max(xData) + 1)

        scatterData(xValues, yValues, yData[0])
        if fitCurve:
            initialGuesses = [1, 1, xMean, yMean]
            bounds = (0, [np.inf, np.inf, np.inf, np.inf])
            coefficients, covar, infodict, errmsg, ier = scipy.optimize.curve_fit(function, xValues, yValues, p0=initialGuesses, bounds=bounds, method='trf', full_output=True)
            graphCurveFit(coefficients, function, xData)

            if includeErrorBars:
                # calculate residuals
                residuals = infodict['fvec']
                # plot error bars
                plt.errorbar(xValues, yValues, yerr=np.abs(residuals), fmt='o', label="Error Bars")

    # set graph limits
    ax.set_ylim([0, yLim])
    ax.set_xlim([min(xData) - 1, max(xData) + 1])


def plotSingleCountry(
        xData: [],
        yData: [],
        fitCurve: bool,
        includeErrorBars: bool,
        function: callable,
        deScalingFactor: int,
        dataLabel: str,
        delimiter: str
) -> None:
    # plot data points for United States
    xValues, yValues = getXYValues(xData, yData, delimiter)
    xMean, yMean = statistics.mean(xValues), statistics.mean(yValues)
    yValues = [y / deScalingFactor for y in yValues]

    # set graph limits
    ax = plt.gca()
    ax.set_ylim([0, (max(yValues) + yMean) / deScalingFactor])
    ax.set_xlim([min(xData) - 1, max(xData) + 1])

    scatterData(xValues, yValues, dataLabel)

    if fitCurve:
        # plot fitted curve
        initialGuesses = [1, 1, xMean, yMean]
        bounds = (0, [np.inf, np.inf, np.inf, np.inf])
        coefficients, covar, infodict, errmsg, ier = scipy.optimize.curve_fit(function, xValues, yValues, p0=initialGuesses, bounds=bounds, method='trf', full_output=True)
        graphCurveFit(coefficients, function, xData)

        if includeErrorBars:
            # calculate residuals
            residuals = infodict['fvec']
            # plot error bars
            plt.errorbar(xValues, yValues, yerr=np.abs(residuals), fmt='o', label="Error Bars")


def main():
    arr1 = [i for i in range(3)]
    arr2 = [i for i in range(3)]



    # display graph with data points and fitted curve
    plt.legend()
    plt.show()
    plt.close()


if __name__ == '__main__':
    main()