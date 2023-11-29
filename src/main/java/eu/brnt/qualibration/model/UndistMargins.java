package eu.brnt.qualibration.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class UndistMargins {

    private int top;
    private int right;
    private int bottom;
    private int left;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UndistMargins that = (UndistMargins) o;
        return getTop() == that.getTop() && getRight() == that.getRight() && getBottom() == that.getBottom() && getLeft() == that.getLeft();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTop(), getRight(), getBottom(), getLeft());
    }
}
