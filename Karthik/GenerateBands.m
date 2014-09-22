function  [bands] = GenerateBands(total_bands)
band_count = 1;
den = integral(@(y)normpdf(y,0,0.25),0,1);
bands = zeros(1,total_bands);
while(band_count <= total_bands)
    lower_limit = (band_count-1)/total_bands;
    upper_limit = band_count/total_bands;
    integral_func = @(x)normpdf(x,0,0.25);
    num = integral(integral_func,lower_limit,upper_limit);
    bands(band_count) = num/den;
    band_count = band_count+1;
end;
disp(bands);
