function guidedfilter_ehancement(file_location, store_location)
I = double(imread(file_location))/255;
 [w, h, o] = size(I);

 %对rgb不同通道进行引导滤波
 R = I(:,:,1);
 p = R;
 IsmoothR = guidedimgfilter(R, p, 16, 0.1*0.1);
 G = I(:,:,2);
 p = G;
 IsmoothG = guidedimgfilter(G, p, 16, 0.1*0.1);
 B = I(:,:,3);
 p = B;
 IsmoothB = guidedimgfilter(B, p, 16, 0.1*0.1);
 Ismooth = zeros([w, h, o]);
 Ismooth(:,:,1) = IsmoothR;
 Ismooth(:,:,2) = IsmoothG;
 Ismooth(:,:,3) = IsmoothB;

 %高提升滤波
 I_enhanced = (I - Ismooth) * 5 + Ismooth;

 imwrite(I_enhanced,strcat(store_location,'.bmp'));
 imwrite([I,Ismooth,I_enhanced],strcat(store_location,'_analyse.bmp'));