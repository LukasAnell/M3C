import numpy as np
import sympy
from numpy import ndarray


def getJacobian(G: np.matrix) -> np.matrix:
    variables = G[0].free_symbols
    jacobianMatrix = np.matrix(np.empty((len(G), len(variables))))
    for r, function in enumerate(G):
        for c, var in enumerate(variables):
            jacobianMatrix[r][c] = sympy.diff(function, var)
    return jacobianMatrix


def evaluateJ(matrix: np.matrix, x_n: np.matrix) -> np.matrix:
    for i in range(len(matrix)):
        for j in range(len(matrix[i])):
            matrix[i][j] = matrix[i][j].subs([(x, x_n[j])])
    return matrix


def evaluateG(G: np.matrix, x_n: np.matrix) -> np.matrix:
    for i in range(len(G)):
        G[i] = G[i].subs([(x, x_n[0])])
    return G


def makeG(function: sympy.Expr, inputData: np.matrix) -> np.matrix:
    G = np.matrix(np.zeros((len(inputData), 1)))
    for r, (valA, valB) in enumerate(inputData):
        print(valA, valB)
        print(G)
        print(function)
        print(function.subs([(A, valA), (B, valB)]))
        print(G[r][0])
        G[r][0] = function.subs([(A, valA), (B, valB)])
    return G


def findF(G: np.matrix) -> np.matrix:
    e = (G * np.transpose(G)).flatten()[0] * 0.5
    return (G * np.transpose(G)).flatten()[0] * 0.5


def findGradientF(F: sympy.Expr) -> np.matrix:
    variables = F.free_symbols
    gradF = np.matrix(np.empty((len(variables), 1)))
    for i, var in enumerate(variables):
        gradF[i] = sympy.diff(F, var)
    return gradF


def evaluateGradF(gradF: np.matrix, guess: np.matrix) -> np.matrix:
    # add loop for subbing every variable instead of just one
    for i in range(len(gradF)):
        gradF[i] = gradF[i].subs([(x, guess[i])])
    return gradF


def findGamma(G: np.matrix, x_n: np.matrix, x_n1: np.matrix) -> float:
    gradF = findGradientF(findF(G))
    numerator = abs(np.transpose(x_n - x_n1) * (evaluateGradF(gradF, x_n) - evaluateGradF(gradF, x_n1)))
    denominator = np.linalg.norm(evaluateGradF(gradF, x_n) - evaluateGradF(gradF, x_n1)) ** 2
    return float(numerator / denominator)


def iterate(G: [], J: [[]], errorMargin: float) -> []:
    numVariables = len(G[0].free_symbols)
    x_n = np.zeros(numVariables)
    x_n1 = np.ones(numVariables)
    r = 100
    while r > errorMargin:
        gamma = findGamma(G, x_n, x_n1)
        temp = x_n1
        x_n1 = x_n - gamma * np.matrix(evaluateJ(J, x_n))
        x_n = temp
        r = np.linalg.norm(x_n1 - x_n)
    return list(x_n1)


def main():
    global A, x, B
    A, x, B = sympy.symbols('A x B')
    function = A * x - B
    inputData = [[1, 5], [2, 8], [3, 11]]
    G = makeG(function, inputData)
    findF(G)
    J = getJacobian(G)
    result = iterate(G, J, 0.0001)
    print(result) # expected [3.0, 4.0]


if __name__ == '__main__':
    main()
