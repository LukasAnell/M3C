from matplotlib import pyplot as plt
import scipyGradientDescent
from classSciPyGradientDescent import SciPyGradientDescent
import numpy as np


def plotEBikeSales(
        included: [str],
        fitCurve: bool,
        includeErrorBars: bool,
        curveFunction: callable,
        deScalingFactor: int,
        delimiter: str
) -> None:
    xCol, yColList = scipyGradientDescent.getDataPoints("Spreadsheets/TCP23_data_vetted.xlsx")
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


def plotTemperatureVsElectricityConsumption(
        fitCurve: bool,
        includeErrorBars: bool,
        curveFunction: callable,
        deScalingFactor: int,
        delimiter: str
) -> None:
    temperatureFilepath = "Spreadsheets/Memphis 2024 Temperature by Month - Sheet1.csv"
    electricityConsumptionFilepath = "Spreadsheets/Memphis Elec Consumption - Monthly electricity consumption for USA.csv"
    # temperatureData = scipyGradientDescent.getDataPointsCSV(temperatureFilepath)
    # electricityConsumptionData = scipyGradientDescent.getDataPointsCSV(electricityConsumptionFilepath)
    sgd = SciPyGradientDescent(temperatureFilepath, electricityConsumptionFilepath)
    allDataTuple = sgd.getDataPointsCSV()
    temperatureData = [array[2] for array in allDataTuple[0]]
    electricityConsumptionData = [int(array[1].replace(',', '')) for array in allDataTuple[1]]

    initialGuesses = [10_000_000_000, 0.0001, -1, -1]

    coefficients = sgd.plotSingleData(
        temperatureData,
        electricityConsumptionData,
        fitCurve,
        includeErrorBars,
        curveFunction,
        initialGuesses,
        deScalingFactor,
        "Plotted Points",
        delimiter
    )

    rSquared = sgd.calculateRSquared(temperatureData, electricityConsumptionData, curveFunction, coefficients)

    ax = plt.gca()

    plt.title("Temperature vs Electricity Consumption")
    plt.xlabel("Temperature (°F)")
    plt.ylabel("Electricity Consumption")
    plt.text(ax.get_xlim()[0], 0, f"R² = {rSquared}", fontsize=14, antialiased=True, verticalalignment='bottom', horizontalalignment='left')
    plt.legend()
    plt.show()
    plt.close()


def plotTemperatureVsElectricityDemand(
        fitCurve: bool,
        includeErrorBars: bool,
        curveFunction: callable,
        deScalingFactor: int,
        delimiter: str
) -> None:
    temperatureFilepath = "Spreadsheets/Memphis Climate Jun-Aug - Sheet1.csv"
    electricityDemandFilepath = "Spreadsheets/Tennessee region electricity overview Jun-Aug.csv"
    sgd = SciPyGradientDescent(temperatureFilepath, electricityDemandFilepath)
    allDataTuple = sgd.getDataPointsCSV()
    temperatureData = [x for x in np.transpose(allDataTuple[0])[1]]
    electricityDemandData = [x for x in np.transpose(allDataTuple[1])[2]]

    initialGuesses = [-1, -1, -1]

    coefficients = sgd.plotSingleData(
        temperatureData,
        electricityDemandData,
        fitCurve,
        includeErrorBars,
        curveFunction,
        initialGuesses,
        deScalingFactor,
        "Plotted Points",
        delimiter
    )

    ax = plt.gca()

    if fitCurve:
        rSquared = sgd.calculateRSquared(temperatureData, electricityDemandData, curveFunction, coefficients)
        plt.text(ax.get_xlim()[0], 0, f"R² = {rSquared}", fontsize=14, antialiased=True, verticalalignment='bottom', horizontalalignment='left')

    plt.title("Temperature vs Electricity Demand")
    plt.xlabel("Temperature (°F)")
    plt.ylabel("Electricity Demand (MWh)")
    plt.legend()
    plt.show()
    plt.close()


def plotTemperatureVsTime(
        fitCurve: bool,
        includeErrorBars: bool,
        curveFunction: callable,
        deScalingFactor: int,
        delimiter: str
) -> None:
    temperatureFilepath = "Spreadsheets/Memphis Climate and Electricity 2019-2024 - Sheet1.csv"
    timeFilepath = "Spreadsheets/Time - Sheet1.csv"
    sgd = SciPyGradientDescent(temperatureFilepath, timeFilepath)
    allDataTuple = sgd.getDataPointsCSV()
    temperatureData = [x for x in np.transpose(allDataTuple[0])[1]]
    timeData = [x for x in np.transpose(allDataTuple[1])[0]]

    initialGuesses = [-1, -1, -1]

    coefficients, residuals = sgd.plotSingleData(
        timeData,
        temperatureData,
        fitCurve,
        includeErrorBars,
        curveFunction,
        initialGuesses,
        deScalingFactor,
        "Plotted Points",
        delimiter
    )

    ax = plt.gca()

    if fitCurve:
        rSquared = sgd.calculateRSquared(residuals  , temperatureData, curveFunction, coefficients)
        plt.text(ax.get_xlim()[0], 0, f"R² = {rSquared}", fontsize=14, antialiased=True, verticalalignment='bottom', horizontalalignment='left')

    plt.title("Temperature vs Time")
    plt.xlabel("Time (months)")
    plt.ylabel("Temperature (°F)")
    plt.legend()
    plt.show()
    plt.close()


def plotDewPointVsTime(
        fitCurve: bool,
        includeErrorBars: bool,
        curveFunction: callable,
        deScalingFactor: int,
        delimiter: str
) -> None:
    dewPointFilepath = "Spreadsheets/Memphis Climate and Electricity 2019-2024 - Sheet1.csv"
    timeFilepath = "Spreadsheets/Time - Sheet1.csv"
    sgd = SciPyGradientDescent(dewPointFilepath, timeFilepath)
    allDataTuple = sgd.getDataPointsCSV()
    dewPointData = [x for x in np.transpose(allDataTuple[0])[2]]
    timeData = [x for x in np.transpose(allDataTuple[1])[0]]

    initialGuesses = [-1, -1, -1]

    coefficients, residuals = sgd.plotSingleData(
        timeData,
        dewPointData,
        fitCurve,
        includeErrorBars,
        curveFunction,
        initialGuesses,
        deScalingFactor,
        "Plotted Points",
        delimiter
    )

    ax = plt.gca()

    if fitCurve:
        rSquared = sgd.calculateRSquared(residuals, dewPointData, curveFunction, coefficients)
        plt.text(ax.get_xlim()[0], 0, f"R² = {rSquared}", fontsize=14, antialiased=True, verticalalignment='bottom', horizontalalignment='left')

    plt.title("Dew Point vs Time")
    plt.xlabel("Time (months)")
    plt.ylabel("Dew Point (°F)")
    plt.legend()
    plt.show()
    plt.close()


def plotWindVsTime(
        fitCurve: bool,
        includeErrorBars: bool,
        curveFunction: callable,
        deScalingFactor: int,
        delimiter: str
) -> None:
    windFilepath = "Spreadsheets/Memphis Climate and Electricity 2019-2024 - Sheet1.csv"
    timeFilepath = "Spreadsheets/Time - Sheet1.csv"
    sgd = SciPyGradientDescent(windFilepath, timeFilepath)
    allDataTuple = sgd.getDataPointsCSV()
    windData = [x for x in np.transpose(allDataTuple[0])[3]]
    timeData = [x for x in np.transpose(allDataTuple[1])[0]]

    initialGuesses = [-1, -1, -1]

    coefficients, residuals = sgd.plotSingleData(
        timeData,
        windData,
        fitCurve,
        includeErrorBars,
        curveFunction,
        initialGuesses,
        deScalingFactor,
        "Plotted Points",
        delimiter
    )

    ax = plt.gca()

    if fitCurve:
        rSquared = sgd.calculateRSquared(residuals, windData, curveFunction, coefficients)
        plt.text(ax.get_xlim()[0], 0, f"R² = {rSquared}", fontsize=14, antialiased=True, verticalalignment='bottom', horizontalalignment='left')

    plt.title("Wind Speed vs Time")
    plt.xlabel("Time (months)")
    plt.ylabel("Wind Speed (mph)")
    plt.legend()
    plt.show()
    plt.close()


def plotAvgElectricityDemandVsTime(
        fitCurve: bool,
        includeErrorBars: bool,
        curveFunction: callable,
        deScalingFactor: int,
        delimiter: str
) -> None:
    electricityDemandFilepath = "Spreadsheets/Memphis Climate and Electricity 2019-2024 - Sheet1.csv"
    timeFilepath = "Spreadsheets/Time - Sheet1.csv"
    sgd = SciPyGradientDescent(electricityDemandFilepath, timeFilepath)
    allDataTuple = sgd.getDataPointsCSV()
    electricityDemandData = [x for x in np.transpose(allDataTuple[0])[3]]
    timeData = [x for x in np.transpose(allDataTuple[1])[0]]

    initialGuesses = [-1, -1, -1]

    coefficients, residuals = sgd.plotSingleData(
        timeData,
        electricityDemandData,
        fitCurve,
        includeErrorBars,
        curveFunction,
        initialGuesses,
        deScalingFactor,
        "Plotted Points",
        delimiter
    )

    ax = plt.gca()

    if fitCurve:
        rSquared = sgd.calculateRSquared(residuals, electricityDemandData, curveFunction, coefficients)
        plt.text(ax.get_xlim()[0], 0, f"R² = {rSquared}", fontsize=14, antialiased=True, verticalalignment='bottom', horizontalalignment='left')

    plt.title("Average Electricity Demand vs Time")
    plt.xlabel("Time (months)")
    plt.ylabel("Electricity Demand (MWh)")
    plt.legend()
    plt.show()
    plt.close()


def main():
    # plotEBikeSales(["United States"], True, True, sgd.exponential, 1, '--')
    # plotTemperatureVsElectricityConsumption(True, True, scipyGradientDescent.sinusoidal, 1, '')
    # plotTemperatureVsElectricityDemand(False, True, scipyGradientDescent.linear, 1, '')
    # plotTemperatureVsTime(True, True, scipyGradientDescent.linear, 1, '')
    # plotDewPointVsTime(True, True, scipyGradientDescent.linear, 1, '')
    # plotWindVsTime(True, True, scipyGradientDescent.linear, 1, '')
    plotAvgElectricityDemandVsTime(True, True, scipyGradientDescent.linear, 1, '')

if __name__ == '__main__':
    main()