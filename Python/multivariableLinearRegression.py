import numpy as np
import pandas
from matplotlib import pyplot as plt
from sklearn import linear_model
from sklearn.model_selection import train_test_split


def main():
    testSpreadsheet = pandas.read_csv("Spreadsheets/Heatwave Temps - Memphis 24-Hour Heat Wave updated.csv")
    columnsToGrab = [
        'Time',
        'Temperature (°F)', 'Humidity (%)'
    ]

    desiredData = np.transpose(np.asarray(testSpreadsheet[columnsToGrab]))

    # dependent = desiredData[0]
    # independent = desiredData[1:]

    dependent = [i for i in range(10, -10, -1)]
    independent = [[i for i in range(20)], [2 * i for i in range(20)]]

    X = np.transpose(np.asarray(independent))
    Y = np.transpose(np.asarray(dependent))

    xTrain, xTest, yTrain, yTest = train_test_split(X, Y, test_size=0.25)

    regr = linear_model.LinearRegression()
    regr.fit(xTrain, yTrain)
    print(regr.score(xTest, yTest))

    # yPred = regr.predict(xTest)
    # fig = plt.figure(figsize=(10, 10))
    # ax = fig.add_subplot(111, projection='3d')
    # ax.scatter(xTest[:, 0], xTest[:, 1], yTest, color='red')
    # ax.scatter(xTest[:, 0], xTest[:, 1], yPred, color='blue')
    # ax.plot(xTest[:, 0], xTest[:, 1], yPred, color='blue')
    # plt.show()


if __name__ == "__main__":
    main()