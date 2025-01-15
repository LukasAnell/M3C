import numpy as np
import numdifftools as nd
from scipy import optimize
import matplotlib.pyplot as plt


def model(x, A, nu0, alpha):
    return A * np.power((x / nu0), 1. * alpha) * np.power((1 + x / nu0), (-4. * alpha))


def gradient_factory(model, x, y):
    def wrapped(p):
        return 0.5 * np.sum(np.power(y - model(x, *p), 2.))

    return nd.Gradient(wrapped)


np.random.seed(1234567)
p0 = (6, 2, 1)
nu = np.linspace(0.05, 1.0, 200)
xi = model(nu, *p0) + 0.05 * np.random.normal(size=nu.size)


def gradient_descent(model, x, y, p0, tol=1e-16, maxiter=500, rate=0.001, atol=1e-10, rtol=1e-8):
    gradient = gradient_factory(model, x, y)

    p = np.array(p0)
    dp = gradient(p)

    for _ in range(maxiter):

        # Update gradient descent:
        p_ = p - rate * dp
        dp_ = gradient(p_)

        # Update rate:
        Dp_ = p_ - p
        Ddp_ = dp_ - dp
        rate = np.abs(Dp_.T @ Ddp_) / np.power(np.linalg.norm(Ddp_), 2)

        # Break when precision is reached:
        if np.allclose(p, p_, atol=atol, rtol=rtol):
            break

        # Next iteration:
        p = p_
        dp = dp_

    else:
        raise RuntimeError("Max Iteration (maxiter=%d) reached" % maxiter)

    return p


p = gradient_descent(model, nu, xi, [1., 1., 1.])
# array([5.82733367, 2.06411618, 0.98227514])
