%快速引导平滑滤波
function fastguided_smoothing(file_location, store_location)
   I = double(imread(file_location))/255;
   p = I;
   
   %快速引导滤波
   Ismooth4 = fastguidedfilter(I, p, 4, 0.2*0.2);
   imwrite(Ismooth4,strcat(store_location, '.bmp'));
