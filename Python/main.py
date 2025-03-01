from matplotlib import pyplot as plt
import scipyGradientDescent
from classSciPyGradientDescent import SciPyGradientDescent


def plotEBikeSales(
        included: [str],
        fitCurve: bool,
        includeErrorBars: bool,
        curveFunction: callable,
        deScalingFactor: int,
        delimiter: str
) -> None:
    xCol, yColList = scipyGradientDescent.getDataPoints("TCP23_data_vetted.xlsx")
    yearData = xCol[1]
    countryNameDict = {
        "United States": 0,
        "Europe": 1,
        "France": 2,
        "China": 3,
        "India": 4,
        "Japan": 5
    }
    unitsSoldList = yColList[countryNameDict["United States"]]
    dataLabel = unitsSoldList[0]

    if len(included) > 1:
        # use plotAllCountries function
        # use the included list to filter the yColList
        scipyGradientDescent.plotAllCountries(
            yearData,
            [yColList[countryNameDict[country]] for country in included],
            fitCurve,
            includeErrorBars,
            curveFunction,
            deScalingFactor,
            delimiter
        )
    else:
        # use plotSingleCountry function
        scipyGradientDescent.plotSingleCountry(
            yearData,
            unitsSoldList[1],
            fitCurve,
            includeErrorBars,
            curveFunction,
            deScalingFactor,
            dataLabel,
            delimiter
        )
    plt.legend()
    plt.show()
    plt.close()


def newPlotEBikeSales(
        included: [str],
        fitCurve: bool,
        includeErrorBars: bool,
        curveFunction: callable,
        deScalingFactor: int,
        delimiter: str
) -> None:
    sgd = SciPyGradientDescent("TCP23_data_vetted.xlsx")
    yearData = sgd.xData[1]
    countryNameDict = {
        "United States": 0,
        "Europe": 1,
        "France": 2,
        "China": 3,
        "India": 4,
        "Japan": 5
    }

    if len(included) > 1:
        # use plotMultipleDataSets function
        # use the included list to filter the yColList
        sgd.plotMultipleDataSets(
            yearData,
            [sgd.yDataList[countryNameDict[country]] for country in included],
            fitCurve,
            includeErrorBars,
            curveFunction,
            deScalingFactor,
            delimiter
        )
    else:
        # use plotSingleData function
        sgd.plotSingleData(
            yearData,
            sgd.yDataList[countryNameDict["United States"]][1],
            fitCurve,
            includeErrorBars,
            curveFunction,
            deScalingFactor,
            sgd.yDataList[countryNameDict["United States"]][0],
            delimiter
        )

    plt.legend()
    plt.show()
    plt.close()


def main():
    plotEBikeSales(["United States"], True, True, sgd.exponential, 1, '--')


if __name__ == '__main__':
    main()