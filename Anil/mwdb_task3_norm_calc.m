function mwdb_task3_norm_calc()
A = csvread('LocationMatrix.csv',1,1);
prompt = 'Enter file number: ';
str = input(prompt,'s');
prompt1 = 'Enter avg/diff/word file: ';
str2 = input(prompt1,'s');
fname = '';
if strcmp(str2,'avg') == 1
        fname = strcat('avgn',num2str(str));
        fname = strcat('E:\MWDB\sampledata_P1_F14\sampledata_P1_F14\Epidemic Simulation Datasets\epidemic_word_files\',fname);
end;
if strcmp(str2,'diff') == 1
            fname = strcat('diffn',num2str(str));
        fname = strcat('E:\MWDB\sampledata_P1_F14\sampledata_P1_F14\Epidemic Simulation Datasets\epidemic_word_files\',fname);
end;
if strcmp(str2,'word') == 1
            fname = strcat('n',num2str(str));
        fname = strcat('E:\MWDB\sampledata_P1_F14\sampledata_P1_F14\Epidemic Simulation Datasets\epidemic_word_files\',fname);
end;
fname = strcat(fname,'.csv');
M = csvread(fname);
[mrows, mcols] = size(M);
% norm_matrix = zeros(mrows,1);
for i=1:mrows
    norm_matrix(i,1)  = norm(M(i,4:mcols));
end;
[nrows, ncols] = size(norm_matrix);
[c, maxloc] = max(norm_matrix);
csvwrite('maxval_states.csv',norm_matrix);
disp('Maximum value state');
disp(M(maxloc,2));
disp(norm_matrix(maxloc));
maxneighbors = find(A(M(maxloc,2),:));
disp('Maximum value neighbors');
disp(maxneighbors);
[c, minloc] = min(norm_matrix);
disp('Minimum value state');
disp(M(minloc,2));
disp(norm_matrix(minloc));
minneighbors = find(A(M(minloc,2),:));
disp('Minimum value neighbors');
disp(minneighbors);
% plotMap(M, 1, M(maxloc,2), M(minloc,2), 5, maxneighbors, minneighbors);
str = strcat(str,'.csv');
str = strcat('E:\MWDB\sampledata_P1_F14\sampledata_P1_F14\Epidemic Simulation Datasets\',str);
S = csvread(str,1,2);
X = 1:size(A,1);
Y = 1:size(S,1);
% Y = fliplr(Y);
imagesc(Y,X,S);
end