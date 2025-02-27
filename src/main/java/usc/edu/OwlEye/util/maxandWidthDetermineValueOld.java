//package usc.edu.OwlEye.util;
//
//import usc.edu.OwlEye.ElementsProperties.*;
//import usc.edu.SALEM.util.Util;
//
//public class maxandWidthDetermineValueOld {
//                case Width.propertyName: {
//        if (prop!=null) {
//            Width widthProp = (Width) prop;
//            String dynamicVal = widthProp.currentDynamicVal;
//            if (dynamicVal != null) {
//                currentDynamicVal = Util.getNumbersFromString(dynamicVal).get(0);
//            }
//        }
//        double mean, min, max;
//        if(currentDynamicVal>0){ // if value exist and it is not 0 or negative we consider it as the min for height
//            min=currentDynamicVal+5;
//        }
//        else{ // for some reason we could not find it so we consider the change value as the min
//            min=currentGeneNumericalVal+5;
//
//        }
//        min=0;
//        mean = currentGeneNumericalVal + 50;
//        mean = currentGeneNumericalVal /2;
//        max = currentGeneNumericalVal + 250;
//        max= currentGeneNumericalVal + 3;
//
//        vals.add(mean);
//        vals.add(min);
//        vals.add(max);
//        break;
//    }
//            case MinHeight.propertyName: {
//        currentGeneNumericalVal = Double.parseDouble(currentVal);
//        double mean = currentGeneNumericalVal + 10;
//        vals.add(mean);
//        vals.add(mean - 25);
//        vals.add(mean + 25);
//        break;
//    }
//
//            case MinWidth.propertyName: {
//        currentGeneNumericalVal = Double.parseDouble(currentVal);
//        double mean = currentGeneNumericalVal + 10;
//        vals.add(mean);
//        vals.add(mean - 25);
//        vals.add(mean + 25);
//        break;
//    }
//            case MaxHeight.propertyName: {
//        currentGeneNumericalVal = Double.parseDouble(currentVal);
//        double mean = currentGeneNumericalVal + 10;
//        vals.add(mean);
//        vals.add(mean - 25);
//        vals.add(mean + 25);
//        break;
//    }
//
//            case MaxWidth.propertyName: {
//        currentGeneNumericalVal = Double.parseDouble(currentVal);
//        double mean = currentGeneNumericalVal + 10;
//        vals.add(mean);
//        vals.add(mean - 25);
//        vals.add(mean + 25);
//        break;
//    }
//}
