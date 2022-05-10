from enum import Enum
from typing import Union

class Type(Enum):
    NUMBER = 1
    BOOLEAN = 2
    STRING = 3

# Digest = tuple[Type, (float | bool | str)]
Digest = tuple[Type, Union[float, bool, str]]
def digest(raw: str) -> Digest:
    if raw in ("true", "false"):
        return Type.BOOLEAN, raw[0] == "t"
    
    try:
        return Type.NUMBER, float(raw)
    except ValueError:
        return Type.STRING, raw

class Data:
    def __init__(self, time: float, raw: str) -> None:
        self.time = time
        self.type, self.value = digest(raw)
    
    def __str__(self):
        return f"{self.value} at {self.time}"

    def __repr__(self):
        return f"Data({str(self)})"
    