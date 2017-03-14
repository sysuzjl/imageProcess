function guidedfilter_smoothing(file_location, store_location) 
I = double(imread(file_location))/255;
 [w, h] = size(I);
 p = I;
 %平滑图像，使用9个不同测试参数
 Ismooth = guidedimgfilter(I, p, 2, 0.1*0.1);
 imwrite(Ismooth,strcat(store_location, '1.bmp'));

 Ismooth1 = guidedimgfilter(I, p, 2, 0.2*0.2);
 imwrite(Ismooth1,strcat(store_location, '2.bmp'));

 Ismooth2 = guidedimgfilter(I, p, 2, 0.4*0.4);
 imwrite(Ismooth2,strcat(store_location, '3.bmp'));

 Ismooth3 = guidedimgfilter(I, p, 4, 0.1*0.1);
 imwrite(Ismooth3,strcat(store_location, '4.bmp'));

 Ismooth4 = guidedimgfilter(I, p, 4, 0.2*0.2);
 imwrite(Ismooth4,strcat(store_location, '5.bmp'));

 Ismooth5 = guidedimgfilter(I, p, 4, 0.4*0.4);
 imwrite(Ismooth5,strcat(store_location, '6.bmp'));

 Ismooth6 = guidedimgfilter(I, p, 8, 0.1*0.1);
 imwrite(Ismooth6,strcat(store_location, '7.bmp'));

 Ismooth7 = guidedimgfilter(I, p, 8, 0.2*0.2);
 imwrite(Ismooth7,strcat(store_location, '8.bmp'));

 Ismooth8 = guidedimgfilter(I, p, 8, 0.4*0.4);
 imwrite(Ismooth8,strcat(store_location, '9.bmp'));
 imwrite([Ismooth,Ismooth1,Ismooth2;Ismooth3,Ismooth4,Ismooth5;Ismooth6,Ismooth7,Ismooth8], strcat(store_location, '_analyse.bmp'));