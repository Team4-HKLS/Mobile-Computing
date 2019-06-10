import numpy as np
import sys
import time
import random
import math

row = 14 # 4, 6, 4
col = 13
student_num = 182 # seats: 182
advertise_num = 182
round_time = 10 #(s)

eps = 10  
minPts = 3

# dist between students in a desk : 0.6m
# dist between students of side-by-side desk : 1.6m
# dist between students of up-down desk : 1.0m 

def place(num):
    list = []
    for i in range(row*col):
        list.append(i)

    list = random.sample(list, k=num)
    list.sort()

    #print(list)
    return list

def calculate_distance(place_list):
    coor = []
    dist_map = [[9999 for x in range(student_num)] for y in range(student_num)]

    for i in range(len(place_list)):
        e_row = place_list[i] / (col)
        e_col = place_list[i] % (col)
        if (e_col > 8):
            x = (0.6*3 + 1.6 + 0.6*4 + 1.6) + (e_col-9) * 0.6
        elif (e_col > 3):
            x = (0.6*3 + 1.6) + (e_col-4) * 0.6
        else:
            x = (e_col) * 0.6
        
        y = e_row * 1.0

        coor.append((x, y))



    for i in range(student_num):
        for j in range(student_num):
            if (i == j):
                dist_map[i][j] = 9999
            elif (i > j):
                dist_map[i][j] = dist_map[j][i]
            else:
                p1 = coor[i]; p2 = coor[j]
                distance = math.sqrt(((p1[0]-p2[0])**2)+((p1[1]-p2[1])**2))
                dist_map[i][j] = distance

    lst = [x for x in range(student_num)]
    lst = random.sample(lst, k=advertise_num)
    for i in range(student_num):
        if i not in lst:
            dist_map[i] = map(lambda x:9999, dist_map[i])
    
    return dist_map

def getNeighbors(st, dist_map):
    list = []
    for i in range(student_num):
        if (dist_map[st][i] <= eps):
            list.append(i)

    return list 
            
def main(argv):
    # Place students to the classroom
    place_list = place(student_num)

    # Calculate the distance between each pair of students
    dist_map = calculate_distance(place_list)

    # DBSCAN
    C = 0
    S = []
    label = {}
    for P in range(student_num):
        if (P in label):
            continue
        neighbors = getNeighbors(P, dist_map)
        if (len(neighbors) < minPts):
            label[P] = -2 # -2: Noise point
            continue

        C = C + 1
        label[P] = C
        S = neighbors
        S.append(P)

        i = -1

#        for i in range(len(S)):
        while (i+1) < len(S):
            i += 1
    #        print 'len S: %d'%(len(S))
            Q = S[i]
   #         print 'Q: %d'%(Q)
  #          print 'i: %d'%(i)
            if (Q in label and label[Q] == -2):
                label[Q] = C
            if (Q in label):
                #print 'i: %d'%(i)
                continue
            label[Q] = C
            N = getNeighbors(Q, dist_map)
 #           print 'N of %d: '%Q, N
            if (len(N) >= minPts):
                S.extend(filter(lambda x:x not in S, N))
 #               print 'S: ', S

    for i in range(1, C+1):
        idx_list = filter(lambda x:label[x]==i, (j for j in range(student_num)))
        print '--Cluster %d (#: %d):'%(i, len(idx_list))
        print map(lambda x:place_list[x], idx_list)

    if -2 in label.values():
        lst = filter(lambda x:label[x]==-2, label.keys())
        lst = map(lambda x:place_list[x], lst)
        print '--Noise nodes (#: %d):'%(len(lst))
        print lst

    print (round_time)
    print (advertise_num)

    print 'time: %ds\n'%(round_time * advertise_num)

#    print 'dist 8 to 9:', dist_map[place_list.index(8)][place_list.index(9)]

if __name__ == '__main__':
    main(sys.argv)

