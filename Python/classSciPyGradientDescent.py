import inspect
import statistics

import numpy as np
import pandas as pd
import scipy.optimize
from matplotlib import pyplot as plt


class SciPyGradientDescent:
    def __init__(self, filePath: str):
        self.filePath = filePath
        self.xData, self.yDataList = self.getDataPoints()


    def getDataPoints(self) -> tuple:
        # read data from Excel file
        data = np.transpose(pd.read_excel(self.filePath).values)
        # extract x and y data
        xDataTitle = data[0][1]
        yDataTitles = [inner[0] for inner in data]
        # create tuples for x and y data
        # xDataTuple is a tuple with the first element being the x-axis title and the second element being the x-axis data
        # yDataTupleList is a list of tuples with the first element being the y-axis title and the second element being the y-axis data
        xDataTuple = (xDataTitle, data[0][2:])
        yDataTupleList = [(yDataTitles[i], data[i][2:]) for i in range(1, len(yDataTitles))]
        return xDataTuple, yDataTupleList


    def getXYValues(self, xData: [], yData: [], delimiter: str) -> tuple:
        # filter out data points with a delimiter
        xValues = [float(xData[i]) for i in range(len(xData)) if yData[i] != delimiter]
        yValues = [float(yData[i]) for i in range(len(yData)) if yData[i] != delimiter]
        return xValues, yValues


    def getXYMean(self, xValues: [], yValues: []) -> tuple:
        xMean, yMean = statistics.mean(xValues), statistics.mean(yValues)
        return xMean, yMean


    def graphCurveFit(self, coefficients: [float], function: callable, xValues: []) -> None:
        # plot fitted curve
        xData = np.linspace(min(xValues) - 1, max(xValues) + 1, 1000)
        yData = function(xData, *coefficients)
        plt.plot(xData, yData, label="Fitted Curve")


    def scatterData(self, xData: [], yData: [], yLabel: str) -> None:
        # scatter data points
        plt.scatter(xData, yData, label=yLabel)


    def plotSingleData(self, xData: [], yData: [], fitCurve: bool, includeErrorBars: bool, function: callable, deScalingFactor: int, dataLabel: str, delimiter: str) -> None:
        xValues, yValues = self.getXYValues(xData, yData, delimiter)
        xMean, yMean = self.getXYMean(xValues, yValues)
        yValues = [yValue / deScalingFactor for yValue in yValues]

        ax = plt.gca()
        ax.set_ylim([0, (max(yValues) + yMean) / deScalingFactor])
        ax.set_xlim([min(xData) - 1, max(xData) + 1])

        self.scatterData(xValues, yValues, dataLabel)

        if fitCurve:
            initialGuessses = [1 for i in range(0, len(inspect.signature(function).parameters) - 1)] + [xMean, yMean]
            bounds = (0, [np.inf for i in range(0, len(initialGuessses))])
            coefficients, covar, infodict, errmsg, ier = scipy.optimize.curve_fit(function, xValues, yValues, p0=initialGuesses, bounds=bounds, method='trf', full_output=True)
            self.graphCurveFit(coefficients, function, xData)

            if includeErrorBars:
                residuals = infodict['fvec']
                plt.errorbar(xValues, yValues, yerr=np.abs(residuals), fmt='0', label="Error Bars")


    def plotMultipleDataSets(self, xData: [], yDataList: [[]], fitCurve: bool, includeErrorBars: bool, function: callable, deScalingFactor: int, delimiter: str) -> None:
        ax = plt.gca()
        yLim = 0
        xLim = 0

        for yData in yDataList:
            self.plotSingleData(xData, yData, fitCurve, includeErrorBars, function, deScalingFactor, yData[0], delimiter)
            xValues, yValues = self.getXYValues(xData, yData[1], delimiter)
            xMean, yMean = self.getXYMean(xValues, yValues)
            yValues = [yValue / deScalingFactor for yValue in yValues]
            yLim = max(yLim, (max(yValues) + yMean) / deScalingFactor)
            xLim = max(xLim, max(xData) + 1)

        ax.set_ylim([0, yLim])
        ax.set_xlim([min(xData) - 1, max(xData) + 1])
