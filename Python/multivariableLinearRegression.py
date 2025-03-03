import numpy as np
import pandas as pd
from sklearn import linear_model
from sklearn.metrics import r2_score


def sensitivityAnalysis(independentData, dependentData, trials=11, sampleSize=5):
    sampleIndices = np.random.choice(len(dependentData), sampleSize, replace=False)
    samplePoints = [dependentData[i] for i in sampleIndices]

    percentDifferences = []
    deviations = []
    for _ in range(trials):
        # Vary each independent data point by ±5%
        variedIndependentData = independentData * (1 + np.random.uniform(-0.05, 0.05, np.shape(independentData)))

        model = linear_model.LinearRegression()
        model.fit(variedIndependentData, dependentData)
        variedYPredict = model.predict(variedIndependentData)

        # Compare original points to varied points
        variedSamplePoints = [variedYPredict[i] for i in sampleIndices]

        deviations.append(np.max(np.abs(np.subtract(samplePoints, variedSamplePoints))))
        percentDifferences.append(np.mean(np.abs(np.divide(np.subtract(samplePoints, variedSamplePoints), samplePoints))))

    percentDifference = np.mean(percentDifferences)
    deviations = np.max(deviations)
    print(f"Percent difference in sample points: {100 * percentDifference}%")
    print(f"Max deviation in sample points: {deviations}")
    return percentDifference, deviations


def findRSquaredOfElectricityDemand():
    independentSpreadsheet = pd.read_csv("Spreadsheets/Memphis Climate Jun-Aug - Sheet1.csv")
    dependentSpreadsheet = pd.read_csv("Spreadsheets/Tennessee region electricity overview Jun-Aug.csv")
    # dependent: electricity demand
    # independent: temperature, humidity, dew point, wind speed, time (day)
    independentColsToGrab = [
        'Temperature (°F)', 'Dew Point (°F)', 'Wind Speed (mph)'
    ]
    dependentColsToGrab = [
        'Demand (MWh)'
    ]

    dependentData = [x for x in np.transpose(np.asarray(dependentSpreadsheet[dependentColsToGrab]))[0]]
    independentData = [[1] + [y for y in x] for x in np.asarray(independentSpreadsheet[independentColsToGrab])]

    indices = np.argsort(dependentData)
    independentData = [independentData[i] for i in indices]
    dependentData.sort()

    model = linear_model.LinearRegression()
    model.fit(independentData, dependentData)
    yPredict = model.predict(independentData)

    # find R^2
    originalR2 = r2_score(dependentData, yPredict)
    print("R^2: ", originalR2)

    return independentData, dependentData, yPredict


def findRSquaredElectricityDemandOverLongTime():
    spreadsheet = pd.read_csv("Spreadsheets/Memphis Climate and Electricity 2019-2024 - Sheet1.csv")
    electricityDemandVsTimeExtrapolation = pd.read_csv("extrapolatedAvgElectricityDemandVsTime.csv")
    dewPointVsTimeExtrapolation = pd.read_csv("extrapolatedDewPointVsTime.csv")
    temperatureVsTimeExtrapolation = pd.read_csv("extrapolatedTemperatureVsTime.csv")
    windSpeedVsTimeExtrapolation = pd.read_csv("extrapolatedWindVsTime.csv")
    # dependent: electricity demand
    # independent: temperature, humidity, dew point, wind speed
    independentColsToGrab = [
        'Avg Temperature max (°F)', 'Dew Point', 'Wind (mph)'
    ]
    dependentColsToGrab = [
        'Avg Electricity Demand (MWh)'
    ]

    extraDependentData = [1] + [[y for y in x] for x in np.transpose(np.asarray(electricityDemandVsTimeExtrapolation))[1]]

    extraIndependentData = [
        [[y for y in x] for x in np.asarray(temperatureVsTimeExtrapolation[['Time', 'Avg Temperature max (°F)']])],
        [[y for y in x] for x in np.asarray(dewPointVsTimeExtrapolation[['Time', 'Dew Point']])],
        [[y for y in x] for x in np.asarray(windSpeedVsTimeExtrapolation[['Time', 'Wind (mph)']])]
    ]

    dependentData = [x for x in np.transpose(np.asarray(spreadsheet[dependentColsToGrab]))[0]] + extraDependentData
    independentData = [[1] + [y for y in x] for x in np.asarray(spreadsheet[independentColsToGrab])] + extraIndependentData
    indices = np.argsort(dependentData)
    independentData = [independentData[i] for i in indices]
    dependentData.sort()

    model = linear_model.LinearRegression()
    model.fit(independentData, dependentData)
    yPredict = model.predict(independentData)

    # find R^2
    originalR2 = r2_score(dependentData, yPredict)
    print("R^2: ", originalR2)

    return independentData, dependentData, yPredict


def main():
    independentData, dependentData, yPredict = findRSquaredOfElectricityDemand()
    percentDifference, maxDeviation = sensitivityAnalysis(independentData, dependentData, trials=1000, sampleSize=5)
    maxPredicted = max(yPredict)
    print(maxPredicted * (1 + percentDifference), maxPredicted + maxDeviation)

    print()

    independentData, dependentData, yPredict = findRSquaredElectricityDemandOverLongTime()
    percentDifference, maxDeviation = sensitivityAnalysis(independentData, dependentData, trials=1000, sampleSize=5)
    maxPredicted = max(yPredict)
    print(maxPredicted * (1 + percentDifference), maxPredicted + maxDeviation)


if __name__ == "__main__":
    main()