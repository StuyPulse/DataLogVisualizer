from enum import Enum, auto
from typing import Union


class Type(Enum):
    NUMBER = auto()
    BOOLEAN = auto()
    STRING = auto()

Value = Union[float, bool, str] # float | bool | str
Digest = tuple[Type, Value]
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
    
    def __str__(self) -> str:
        return f"{self.value} at {self.time}"

    def __repr__(self) -> str:
        return f"Data({str(self)})"
    