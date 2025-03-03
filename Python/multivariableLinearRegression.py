import numpy as np
import pandas as pd
from matplotlib import pyplot as plt
from pyparsing import originalTextFor
from sklearn import linear_model
from sklearn.metrics import r2_score
from sklearn.model_selection import train_test_split


def sensitivityAnalysis(independentData, dependentData, trials=11, sampleSize=5):
    sampleIndices = np.random.choice(len(dependentData), sampleSize, replace=False)
    samplePoints = [dependentData[i] for i in sampleIndices]

    deviations = []
    for _ in range(trials):
        # Vary each independent data point by ±5%
        variedIndependentData = independentData * (1 + np.random.uniform(-0.05, 0.05, np.shape(independentData)))

        model = linear_model.LinearRegression()
        model.fit(variedIndependentData, dependentData)
        variedYPredict = model.predict(variedIndependentData)

        # Compare original points to varied points
        variedSamplePoints = [variedYPredict[i] for i in sampleIndices]
        deviations.append(np.abs(np.mean(variedSamplePoints) - np.mean(samplePoints)) / np.mean(samplePoints) * 100)

    averageDeviation = np.mean(deviations, axis=0)
    print(f"Average deviation in sample points: {averageDeviation}%")
    # print("Average deviation in random index: ", np.mean(deviations, axis=0))


def findRSquaredOfElectricityDemand():
    independentSpreadsheet = pd.read_csv("Spreadsheets/Memphis Climate Jun-Aug - Sheet1.csv")
    dependentSpreadsheet = pd.read_csv("Spreadsheets/Tennessee region electricity overview Jun-Aug.csv")
    # dependent: electricity demand
    # independent: temperature, humidity, dew point, wind speed, time (day)
    independentColsToGrab = [
        'Temperature (°F)', 'Dew Point (°F)', 'Humidity (%)', 'Wind Speed (mph)'
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
    # dependent: electricity demand
    # independent: temperature, humidity, dew point, wind speed
    independentColsToGrab = [
        'Avg Temperature max (°F)', 'Dew Point', 'Wind (mph)'
    ]
    dependentColsToGrab = [
        'Avg Electricity Demand (MWh)'
    ]

    dependentData = [x for x in np.transpose(np.asarray(spreadsheet[dependentColsToGrab]))[0]]
    independentData = [[1] + [y for y in x] for x in np.asarray(spreadsheet[independentColsToGrab])]

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
    #independentData, dependentData, yPredict = findRSquaredOfElectricityDemand()
    # sensitivityAnalysis(independentData, dependentData, trials=5000, sampleSize=5)

    independentData, dependentData, yPredict = findRSquaredElectricityDemandOverLongTime()
    sensitivityAnalysis(independentData, dependentData, trials=1000, sampleSize=5)


if __name__ == "__main__":
    main()