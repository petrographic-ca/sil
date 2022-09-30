from typing import Iterable

import numpy as np  # pylint: disable=import-error


V_VOID = 0
V_SOLID = 255


def value(it: int, x: int, y: int, z: int) -> int:
    # where-ever more than one dim is at % 3 == 1, make that void.
    if it == 0:
        return V_SOLID

    if (
        (x // (3**(it-1)) % 3 == 1)
        + (y // (3**(it-1)) % 3 == 1)
        + (z // (3**(it-1)) % 3 == 1) > 1  # if only there was a bool way
    ):
        return V_VOID

    return value(it-1, x, y, z)  # hahaha leap of faith! <3


def generate_menger(iterations: int = 1) -> Iterable[np.ndarray]:
    diameter = 3**iterations
    for z in range(diameter):
        yield np.fromiter((
            value(iterations, x, y, z)
            for y in range(diameter)
            for x in range(diameter)
        ), dtype=np.uint8).reshape((diameter, diameter))
