function [new_I] = subsample(I, s)
[w, h] = size(I);
new_w = w/s;
new_h = h/s;
%uint32(new_w)有可能无法整除
new_I = zeros(uint32(new_w), uint32(new_h));
for i = 1:new_w
    k = uint32(i*s);
    u = i*s - double(k);
    if k >= w
        k = w-1;
    elseif k <= 0
        k = 1;
    else
    end
    for j = 1:new_h
        l = uint32(j*s);
        c = j*s - double(l);
        if l >= h
            l = h-1;
        elseif l <= 0
            l = 1;
        else
        end
        %双线性插值
        q11 = I(k, l)*(1.0-c) + I(k, l+1) * 1.0 * c;
        q22 = I(k+1, l)*(1.0-c) + I(k+1, l+1) * 1.0 * c;
        new_I(i,j) = q11*(1.0 - u) + q22*1.0*u;
    end
end
        

