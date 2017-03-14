 function [q] = fastguidedfilter(origin_I, origin_p, origin_radius, eps)
%  I = origin_I;
%  p = origin_p;
%引导图像和输入图像下采样
 I = subsample(origin_I, 4);
 p = subsample(origin_p, 4);
 radius = origin_radius/4;
%  radius = origin_radius;
 
 [w,h] = size(I);
 [originw,originh] = size(origin_I);
%获取窗口大小，并拓充成等大矩阵
 n = meanfilter(ones(w,h), radius);
%对输入图像进行均值滤波计算平均值
 meanI = meanfilter(I,radius)./n;
%对引导图像进行均值滤波计算平均值
 meanP = meanfilter(p,radius)./n;
%计算引导图像方差
 corrI = meanfilter(I.*I,radius)./n;
 varI = corrI - meanI.*meanI;
%计算协方差
 corrIP = meanfilter(I.*p,radius)./n;
 covIP = corrIP - meanI.*meanP;
%获得系数矩阵a
 a = covIP./(varI + eps);
%获得系数矩阵b
 b = meanP - a.*meanI;
%计算矩阵a的均值
 meanA = meanfilter(a, radius)./n;
%计算矩阵b的均值
 meanB = meanfilter(b, radius)./n;
%引导图像和输入图像上采样
 meanA = upsample(meanA, 4, originw, originh);
 meanB = upsample(meanB, 4, originw, originh);
 q = meanA.*origin_I + meanB;
 
%高效的均值滤波器
function [imgdes] = meanfilter(imgsrc, r)
[w,h] = size(imgsrc);
imgdes = zeros(w, h);

%计算列矩阵和
imcum = cumsum(imgsrc, 1);
% imgcum(i+(2*r+1),j)的列累计和- imgcum(i,j)的列累计和就等于imgdes(i,j) ;
imgdes(1:r+1,:) = imcum(1+r:2*r+1,:);
imgdes(r+2:w-r,:) = imcum(2*r+2:w,:) - imcum(1:w-2*r-1, :);
imgdes(w-r+1:w,:) = repmat(imcum(w,:), [r, 1]) - imcum(w-2*r:w-r-1, :);

%计算行矩阵和
imcum = cumsum(imgdes, 2);
% imgcum(i,j+(2*r+1))的行累计和 - imgcum(i,j)的行累计和就等于imgdes(i,j) ;
imgdes(:,1:r+1) = imcum(:, 1+r:2*r+1);
imgdes(:,r+2:h-r) = imcum(:, 2*r+2:h) - imcum(:, 1:h-2*r-1);
imgdes(:,h-r+1:h) = repmat(imcum(:, h), [1, r]) - imcum(:, h-2*r:h-r-1);


 
 
 