function mwdb_task2_a_final_fast(num_files, skip_length)
A = csvread('LocationMatrix.csv',1,1);
[lrows, lcols] = size(A);
winsize = 3;
alpha = 0.345;
wsize = 3+winsize;
for g=1:num_files
        ofname  = strcat('avgn',num2str(g));
        fname = strcat('n',num2str(g));
        fname = strcat('E:\MWDB\sampledata_P1_F14\sampledata_P1_F14\Epidemic Simulation Datasets\epidemic_word_files\',fname);
        ofname = strcat('E:\MWDB\sampledata_P1_F14\sampledata_P1_F14\Epidemic Simulation Datasets\epidemic_word_files\',ofname);
        fname = strcat(fname,'.csv');
        ofname = strcat(ofname,'.csv');
%         disp(strcat('loaded file',fname));
        M = csvread(fname);
        [drows,dcols] = size(M);
        output_matrix = zeros(drows,wsize);
        for i=1:drows
           loc = M(i,2);
           neighbors = find(A(loc,:));
           [nrows, ncols] = size(neighbors);
           weight_vector_nei = zeros(ncols, winsize);
           weight_avg = alpha.*M(i,4:dcols);
                for k=1:ncols
                    if M(i,3) == 1
                        shift = 1;
                    else
                        shift = M(i,3)-M(i-1,3);
                    end;
                    j = skip_length*(neighbors(1,k)-1)+shift;
                    weight_vector_nei(k,:) = M(j,4:dcols);
                end;
        %    disp('weight vector avg of neighbors')
           if ncols ~= 0
               if ncols~=1
                weight_neighbor_avg = sum(weight_vector_nei)./ncols;
                weight_neighbor_avg = (1-alpha).*(weight_neighbor_avg);
               else
                   weight_neighbor_avg = weight_vector_nei;
               end;
            weight_final_avg = weight_avg+weight_neighbor_avg;
            output_matrix(i,1:3)=M(i,1:3);
            output_matrix(i,4:wsize)=weight_final_avg;
           else
            output_matrix(i,1:3)=M(i,1:3);
            output_matrix(i,4:wsize)=weight_avg;
           end;

        %    disp(weight_neighbor_avg);
        %    disp('original vector weight avg');
        %    disp(weight_avg);
        end;
        csvwrite(ofname,output_matrix);
end;
end