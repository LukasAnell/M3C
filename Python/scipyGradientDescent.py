import numpy as np
import scipy.optimize
import matplotlib.pyplot as plt
import pandas as pd


def makeInputPoints(function: callable, range) -> tuple:
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


def graphAll(xData, yData, coefficients, function, range, xLabel):
    plt.scatter(yData, xData, label=xLabel)
    xData = np.linspace(range[0], range[1], 1000)
    yData = function(xData, *coefficients)
    plt.plot(xData, yData, label='curve fit')
    plt.legend()
    plt.show()


def f(x, A=1, B=1, C=1):
    return A / (1 + np.exp(-B * (x - C)))


def plotData(xData, yData, xLabel):
    # xData = [xData[i] for i in range(len(xData)) if xData[i] != '--']
    # xData = [-100 if x == '--' else x for x in xData]
    ax = plt.gca()
    ax.set_ylim([0, 1000])
    # plt.plot(yData, xData, 'ro', label=xLabel)
    plt.scatter(yData, xData, label=xLabel)
    plt.legend()
    # plt.show()


def main():
    # range = (-20, 20)
    # xData, yData = makeInputPoints(f, range)
    # coefficients = scipy.optimize.curve_fit(f, xData, yData)[0]
    # graphAll(xData, yData, coefficients, f, range)

    # ax = plt.gca()
    # ax.set_color_cycle(['red', 'green', 'blue', 'yellow'])
    yData, xData = getDataPoints("TCP23_data_vetted.xlsx")
    # for i in range(len(xData)):
    #     plotData(xData[i][1], yData[1], xData[i][0])
    # plotData(xData[2][1], yData[1], xData[2][0])
    # plt.show()
    currentYData = yData[1]
    currentXData = xData[2]
    xLabel = currentXData[0]
    xDataValues = [currentXData[1][i] for i in range(len(currentXData[1])) if currentXData[1][i] != '--']
    desiredYData = []
    for i in range(len(xDataValues)):
        if xDataValues[i] != '--':
            desiredYData.append(currentYData[i])
    plotData(desiredYData, xDataValues, xLabel)
    plt.show()


if __name__ == '__main__':
    main()