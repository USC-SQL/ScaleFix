package usc.edu.OwlEye.UIModels;

import usc.edu.OwlEye.BuildModels.*;
import usc.edu.OwlEye.OwlEye;
import usc.edu.OwlEye.VH.UI;

public class ConstructUIModels {

    private final UI originalUI;

    public ConstructUIModels(UI originalUI) {
        this.originalUI = originalUI;

    }
    public void constructSRG() {
        //2- Construct SRG
        ConstructSRG constructSRG= ConstructSRG.getInstance();
        SRG srg = constructSRG.buildGraph(this.originalUI);
        OwlEye.setOriginalDefaultUISRG(srg);
    }
    public void constructVSRG() {
        //3- Construct VSRG
        ConstructVSRG constructVSRG = ConstructVSRG.getInstance();
        VSRG vsrg = constructVSRG.buildGraph(this.originalUI);
//        constructVSRG.constructVSRG();
        OwlEye.setOriginalDefaultUIVSRG(vsrg);
    }

    public void constructTRG() {
        //4- Construct TRG
        ConstructTRG constructTRG = ConstructTRG.getInstance();
        TRG trg = constructTRG.buildGraph(this.originalUI);
//        constructVSRG.constructVSRG();
        OwlEye.setOriginalDefaultUITRG(trg);
    }

    public void constructSPLRG() {
        ConstructSPLRG constructSPLRG = ConstructSPLRG.getInstance();
        SPLRG splrg = constructSPLRG.buildGraph(this.originalUI);
//        constructVSRG.constructVSRG();
        OwlEye.setOriginalDefaultUISPLRG(splrg);
    }

//    public void constructWRG() {
//        ConstructWRG constructWRG = ConstructWRG.getInstance();
//        WRG wrg = constructWRG.buildGraph(this.originalUI);
////        constructVSRG.constructVSRG();
//        OwlEye.setOriginalDefaultUIWRG(wrg);
////        OwlEye.setOriginalDefaultUISPLRG(splrg);
//    }
public void constructWRG() {
    ConstructWRG2 constructWRG2 = ConstructWRG2.getInstance();
    WRG wrg = constructWRG2.buildGraph(this.originalUI);
//        constructVSRG.constructVSRG();
    OwlEye.setOriginalDefaultUIWRG(wrg);
//        OwlEye.setOriginalDefaultUISPLRG(splrg);
}
}
