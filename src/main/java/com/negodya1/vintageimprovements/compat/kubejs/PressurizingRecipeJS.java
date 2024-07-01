package com.negodya1.vintageimprovements.compat.kubejs;

public class PressurizingRecipeJS extends ProcessingRecipeJS{

    public PressurizingRecipeJS secondaryInput(int slot){
        json.addProperty("secondaryFluidInputs", slot);
        save();
        return this;
    }
    public PressurizingRecipeJS secondaryOutput(int slot){
        json.addProperty("secondaryFluidResults", slot);
        save();
        return this;
    }


}
