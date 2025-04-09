package fr.mewtrpg.utils.formula;

import fr.mewtrpg.utils.SerializableExpression;
import fr.mewtrpg.utils.VariablesHolder;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

public class FormulaPos extends FormulaVec{
    SerializableExpression yaw, pitch;

    public FormulaPos(SerializableExpression x, SerializableExpression y, SerializableExpression z, SerializableExpression yaw, SerializableExpression pitch) {
        super(x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public FormulaPos(SerializableExpression x, SerializableExpression y, SerializableExpression z) {
        this(x, y, z, new SerializableExpression("0"), new SerializableExpression("0"));
    }

    @Override
    public void apply(VariablesHolder variables) {
        super.apply(variables);
        yaw.getExpression().setVariables(variables.getVariables());
        pitch.getExpression().setVariables(variables.getVariables());
    }

    public Pos getPos(VariablesHolder variables) {
        apply(variables);
        return new Pos(x.getExpression().evaluate(), y.getExpression().evaluate(), z.getExpression().evaluate(), (float) yaw.getExpression().evaluate(), (float) pitch.getExpression().evaluate());
    }
}
