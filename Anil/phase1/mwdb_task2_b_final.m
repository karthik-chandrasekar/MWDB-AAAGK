function mwdb_task2_b_final(num_files, winsize, epidemic_word_file_path)
A = csvread('LocationMatrix.csv',1,1);
[lrows, lcols] = size(A);
alpha = 0.345;
wsize = 3+winsize;
num_files
for g=1:num_files
        ofname  = strcat('diffn',num2str(g));
        fname = strcat('n',num2str(g));
        fname = strcat(epidemic_word_file_path,fname);
        ofname = strcat(epidemic_word_file_path,ofname);
        fname = strcat(fname,'.csv');
        ofname = strcat(ofname,'.csv');
        disp(strcat('loaded file',fname));
        M = csvread(fname);
        [drows,dcols] = size(M);
        output_matrix = zeros(drows,wsize);
        skip_length = drows/lcols;
       for i=1:drows
           loc = M(i,2);
           neighbors = find(A(loc,:));
           [nrows, ncols] = size(neighbors);
           weight_vector_nei = zeros(ncols, winsize);
           weight_avg = M(i,4:dcols);
                for k=1:ncols
                    j = 1;
                    while (j<=drows)
                        if M(j,2) == neighbors(1,k)
                            if M(j,3) == M(i,3) 
                             weight_vector_nei(k,:) = M(j,4:dcols);
                             break;
                            else
                                j=j+1;
                            end;
                        else
                            j = j + skip_length;
                        end;
                    end;  
                end;
           if ncols ~= 0 
               if ncols~=1
                    weight_neighbor_avg = sum(weight_vector_nei)./ncols;
               else
                   weight_neighbor_avg = weight_vector_nei;
               end;
                weight_final_avg = weight_avg-weight_neighbor_avg;
                if weight_avg~=0
                    weight_final_avg = weight_final_avg./weight_avg;
                end;
                output_matrix(i,1:3)=M(i,1:3);
                output_matrix(i,4:wsize)=weight_final_avg;
           else
                weight_final_avg = weight_avg./weight_avg;
                output_matrix(i,1:3)=M(i,1:3);
                output_matrix(i,4:wsize)=weight_final_avg;
           end;
    end;
  csvwrite(ofname,output_matrix);
end;
end