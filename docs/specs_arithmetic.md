### 16. Arithmetic Operations

The `.talk` language supports arithmetic expressions using natural language syntax.

#### Basic Arithmetic

| Operation       | Description                     | Example Syntax                          |
|----------------|---------------------------------|------------------------------------------|
| Addition        | Adds two numbers                | `set total to 5 plus 3`                  |
| Subtraction     | Subtracts second from first     | `set diff to 10 minus 4`                 |
| Multiplication  | Multiplies two numbers          | `set product to 6 times 2`               |
| Division        | Divides first by second         | `set result to 12 divided by 3`          |
| Modulo          | Remainder of division           | `set remainder to 10 modulo 3`           |
| Negation        | Negative value                  | `set negative to negative of x`          |

#### Advanced Arithmetic

| Operation       | Description                     | Example Syntax                          |
|----------------|----------------------------------|------------------------------------------|
| Power           | Raises to a power               | `set squared to 4 to the power of 2`     |
| Absolute        | Absolute value                  | `set abs to absolute of -5`              |
| Round           | Round to nearest integer        | `set rounded to round 3.7`               |
| Floor / Ceil    | Round down or up                | `set down to floor 4.8`, `set up to ceil 4.2` |

#### Notes:
- All arithmetic uses floating point precision by default.
- Invalid operands raise runtime errors unless wrapped in `attempt`.
- Future extensions may include math constants like `pi`, `e`.
```
