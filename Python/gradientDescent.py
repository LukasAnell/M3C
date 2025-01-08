import numpy as np
from sympy import symbols, diff, Expr


def getJacobian(G: []) -> [[]]:
    variables = G[0].free_symbols
    jacobianMatrix = []
    for function in G:
        holderRow = []
        for var in variables:
            holderRow.append(diff(function, var))
        jacobianMatrix.append(holderRow)
    return jacobianMatrix


def evaluate(matrix: [[]], x_n: []) -> [[]]:
    for i in range(len(matrix)):
        for j in range(len(matrix[i])):
            matrix[i][j] = matrix[i][j].subs([(x, x_n[j])])
    return matrix


def makeG(function: Expr, inputData: [[]]) -> []:
    G = []
    for row in inputData:
        G.append(function.subs([(A, row[0])]) - row[1])
    return G


def findGamma(G: [], x_n: [], x_n1: []) -> float:
    x_n = np.array(x_n)
    x_n1 = np.array(x_n1)
    numerator = np.transpose(x_n - x_n1) * np.matrix(evaluate(G, x_n))
    denominator = np.transpose(np.matrix(evaluate(G, x_n))) * np.matrix(evaluate(G, x_n))
    return float(numerator / denominator)


def iterate(G: [], J: [[]], errorMargin: float) -> []:
    numVariables = len(G[0].free_symbols)
    x_n = np.array([0 for _ in range(numVariables)])
    x_n1 = np.array([1 for _ in range(numVariables)])
    r = 100
    while r > errorMargin:
        gamma = findGamma(G, x_n, x_n1)
        temp = x_n1
        x_n1 = x_n - gamma * np.matrix(evaluate(J, x_n))
        x_n = temp
        r = np.linalg.norm(x_n1 - x_n)
    return list(x_n1)


def main():
    global A, x, B
    A, x, B = symbols('A x B')
    function = A * x - B
    inputData = [[1, 5], [2, 8], [3, 11]]
    G = makeG(function, inputData)
    J = getJacobian(G)
    result = iterate(G, J, 0.0001)
    print(result) # expected [3.0, 4.0]


if __name__ == '__main__':
    main()
