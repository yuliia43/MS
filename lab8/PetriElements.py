import numpy as np
import operator


class Place:
    place_id = 1
    min_markers = 100000000000000000000
    max_markers = 0
    avg_markers = 0
    changes_count = 0

    def __init__(self, markers=0, name=None):
        self.__markers = markers
        self.min_markers = 100000000000000000000
        self.max_markers = 0
        self.avg_markers = 0
        self.changes_count = 0
        if name is None:
            self.name = 'P'+ str(Place.place_id)
            Place.place_id += 1
        else:
            self.name = name
        self.count_statistic()

    def increase_markers(self, delta):
        self.__markers += delta
        self.count_statistic()

    def decrease_markers(self, delta):
        self.__markers -= delta
        self.count_statistic()

    def get_markers(self):
        return self.__markers

    def count_statistic(self):
        Place.min_markers = min(self.__markers, Place.min_markers)
        Place.max_markers = max(self.__markers, Place.max_markers)
        Place.avg_markers += self.__markers
        Place.changes_count += 1
        self.min_markers = min(self.__markers, self.min_markers)
        self.max_markers = max(self.__markers, self.max_markers)
        self.avg_markers += self.__markers
        self.changes_count += 1


class Transition:
    transitions_id = 1

    def __init__(self, time_delay=0.0, name=None):
        self.__input_arcs = []
        self.__output_arcs = []
        self.__time_delay = time_delay
        self.__distribution = "exp"
        self.__t_current = 0
        self.__time_deviation = 0
        self.__t_next = float('inf')
        self.priority = 0
        if name is None:
            self.name = 'Transition' + str(Transition.transitions_id)
            Transition.transitions_id += 1
        else:
            self.name = name

    def get_t_next(self):
        return self.__t_next

    def add_input_arc(self, arc):
        self.__input_arcs.append(arc)

    def add_output_arc(self, arc):
        self.__output_arcs.append(arc)

    def set_distribution(self, distribution):
        self.__distribution = distribution

    def could_be_activated(self):
        could_be_activated = True
        for arc in self.__input_arcs:
            if arc.multiplicity > arc.input_element.get_markers():
                could_be_activated = False
                break
        return could_be_activated

    def activation(self, t_current):
        if self.could_be_activated():
            self.__t_current = t_current
            for arc in self.__input_arcs:
                if not arc.is_informational:
                    arc.input_element.decrease_markers(arc.multiplicity)
            self.__t_next = self.__t_current + self.get_delay()

    def post_activation(self):
        for arc in self.__output_arcs:
            arc.output_element.increase_markers(arc.multiplicity)
        self.__t_next = float('inf')

    def get_delay(self):
        if self.__time_delay == 0:
            return self.__time_delay
        if self.__distribution == "exp":
            return np.random.exponential(self.__time_delay)
        elif self.__distribution == "unif":
            if self.__time_deviation != 0:
                return (self.__time_delay-self.__time_deviation +
                        2*self.__time_deviation*np.random.rand())
        elif self.__distribution == "norm":
            if self.__time_deviation != 0:
                return np.random.normal(self.__time_delay, self.__time_deviation)
        return self.__time_delay


class Arc:

    def __init__(self, input_element, output_element, multiplicity=1, is_informational=False):
        self.multiplicity = multiplicity
        self.input_element = input_element
        self.output_element = output_element
        self.is_informational = is_informational
        if isinstance(self.input_element, Transition):
            self.input_element.add_output_arc(self)
        elif isinstance(self.output_element, Transition):
            self.output_element.add_input_arc(self)


class Model:
    verification = False

    def __init__(self, transitions, places):
        self.__transitions = transitions
        self.__places = places
        self.__marker_changes = 0
        self.verification_info = None

    def simulate_for_time(self, time_modelling):
        if not self.verification:
            self.print_markers()
        t_current = 0
        while t_current < time_modelling:
            t_current = self.iterate(t_current)
        if not self.verification:
            self.print_stats()

    def simulate_for_iterations(self, iterations):
        if not self.verification:
            self.print_markers()
        t_current = 0
        current_iteration = 0
        while current_iteration < iterations:
            t_current = self.iterate(t_current)
            current_iteration += 1
        if not self.verification:
            self.print_stats()
        else:
            self.print_verification()

    def iterate(self, t_current):
        #Priorities for future development
        transitions = sorted(self.__transitions, key=operator.attrgetter('priority'), reverse=True)
        #transitions = self.__transitions.copy()
        while len(transitions) != 0:
            for transition in transitions:
                if not transition.could_be_activated():
                    transitions.remove(transition)
                elif transition.priority != 0:
                    transition.activation(t_current)
                    transitions.remove(transition)
                    if not self.verification:
                        print(t_current, "Activated", transition.name)
                        self.print_markers()
            if len(transitions) != 0:
                transition = Model.choose_transition(transitions)
                transition.activation(t_current)
                transitions.remove(transition)
                if not self.verification:
                    print(t_current, "Activated", transition.name)
                    self.print_markers()
        t_next = float('inf')
        for transition in self.__transitions:
            if transition.get_t_next() < t_next:
                t_next = transition.get_t_next()
        if t_next != float('inf'):
            for transition in self.__transitions:
                if transition.get_t_next() == t_next:
                    transition.post_activation()
                    t_current = t_next
                    if not self.verification:
                        print(t_current, transition.name, "finished processing")
                        self.print_markers()
        return t_current

    @staticmethod
    def choose_transition(transitions):
        randNum = np.random.rand()
        idx = int(randNum * len(transitions))
        return transitions[idx]

    def print_markers(self):
        markers_info = ''
        for marker in self.__places:
            markers_info += (marker.name+":"+str(marker.get_markers())+"\t")
        print(markers_info)

    def print_stats(self):
        print("STATISTICS")
        for marker in self.__places:
            print(marker.name, "min markers: ", marker.min_markers)
            print(marker.name, "max markers: ", marker.max_markers)
            print(marker.name, "mean markers: ", marker.avg_markers/marker.changes_count)
        print("Avg min markers: ", Place.min_markers)
        print("Avg max markers: ", Place.max_markers)
        print("Avg mean markers: ", Place.avg_markers/Place.changes_count)

    def print_verification(self):
        verification_str = self.verification_info
        for place in self.__places:
            if not place.name.startswith("P"):
                verification_str += '|{:^15.2f}'.format(place.avg_markers/place.changes_count)
        verification_str += " |"
        print(verification_str)

    @staticmethod
    def print_verification_header():
        verification_str = "| Надх. Д1 | Надх. Д2 |" \
                           " К-сть Д1 для комплектації | К-сть Д2 для комплектації |"
        verification_str += "|{:^15}|{:^15}|{:^15}|{:^15}|".format(
                           " Черга Д1", "Черга Д2", "Порожні секції", "Заповнені секції")
        print(verification_str)


def conveer_task(det1=10, det2=40, complect_d1=20, complect_d2=20):
    p1 = Place(1)
    t1 = Transition(name="Надходження деталей 1-ого типу")
    arc1 = Arc(p1, t1)
    arc2 = Arc(t1, p1)
    p2 = Place(name="Черга Д1")     # Черга деталей 1-ого типу
    arc3 = Arc(t1, p2, det1)

    p3 = Place(1)
    t2 = Transition(name="Надходження деталей 2-ого типу")
    arc4 = Arc(p3, t2)
    arc5 = Arc(t2, p3)
    p4 = Place(name="Черга Д2")     #Черга деталей 2-ого типу
    arc6 = Arc(t2, p4, det2)

    p5 = Place(1)
    t3 = Transition(name="Надходження секції конвеєра")
    arc7 = Arc(p5, t3)
    arc8 = Arc(t3, p5)
    p6 = Place()
    arc9 = Arc(t3, p6)
    t4 = Transition(name="Пропуск секції")
    arc10 = Arc(p6, t4)
    p7 = Place(name="Порожні секції")    #Кількість порожніх секцій

    arc11 = Arc(t4, p7)
    t5 = Transition(name="Комплектація")
    arc11 = Arc(p2, t5, complect_d1)
    arc12 = Arc(p4, t5, complect_d2)
    arc13 = Arc(p6, t5)
    p8 = Place(name="Заповнені секції")  #Кількість заповнених секцій
    arc14 = Arc(t5, p8)
    model = Model([t1, t2, t3, t4, t5], [p1, p2, p3, p4, p5, p6, p7, p8])
    model.verification_info= '| {:^8d} | {:^8d} | {:^25d} | {:^25d} |'\
        .format(det1, det2, complect_d1, complect_d2)
    model.simulate_for_iterations(1000)

def lab8_task1():
    p1 = Place(1)
    t1 = Transition(name="Надходження повідомлення від А")
    Arc(p1, t1)
    Arc(t1, p1)
    p2 = Place()
    Arc(t1, p2)
    t2 = Transition(name="Запит від А на передачу в В")
    Arc(p2, t2)
    p3 = Place()
    Arc(t2, p3)
    t3 = Transition(name="Позитивна відповідь від вузла В вузлу А")
    Arc(p3, t3)
    p4 = Place()
    Arc(t3, p4)
    t4 = Transition(name="Відправка повідомлення вузлом А")
    Arc(p4, t4)
    p5 = Place()
    Arc(t4, p5)
    t5 = Transition(name="Отримання повідомлення вузлом В")
    Arc(p5, t5)
    p6 = Place()
    Arc(t5, p6)
    t6= Transition(name="Прийом сигналу про успішне отримання повідомлення вузлом В")
    Arc(p6, t6)
    p7 = Place(name="Отримані вузлом A повідомлення")
    Arc(t6, p7)


    p8 = Place(1)
    t7 = Transition(name="Надходження повідомлення від B")
    Arc(p8, t7)
    Arc(t7, p8)
    p9 = Place()
    Arc(t7, p9)
    t8 = Transition(name="Запит від B на передачу в A")
    Arc(p9, t8)
    p10 = Place()
    Arc(t8, p10)
    t9 = Transition(name="Позитивна відповідь від вузла A вузлу B")
    Arc(p10, t9)
    p11 = Place()
    Arc(t9, p11)
    t10 = Transition(name="Відправка повідомлення вузлом B")
    Arc(p11, t10)
    p12 = Place()
    Arc(t10, p12)
    t11 = Transition(name="Отримання повідомлення вузлом A")
    Arc(p12, t11)
    p13 = Place()
    Arc(t11, p13)
    t12 = Transition(name="Прийом сигналу про успішне отримання повідомлення вузлом A")
    Arc(p13, t12)
    p14 = Place(name="Отримані вузлом B повідомлення")
    Arc(t12, p14)

    p15 = Place(1, name="Керуючий сигнал")
    Arc(p15, t2)
    Arc(p15, t8)
    Arc(t6, p15)
    Arc(t12, p15)

    model = Model([t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12],
                  [p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15])
    model.simulate_for_iterations(1000)

def lab8_task2(buffer_size=10):
    p1 = Place(1)
    t1 = Transition(name="Надходження")
    Arc(p1, t1)
    Arc(t1, p1)
    p2 = Place()
    Arc(t1, p2)
    t2 = Transition(name="Producer")
    Arc(p2, t2)
    p3 = Place(name="Buffer")
    Arc(t2, p3)
    t3 = Transition(name="Consumer")
    Arc(p3, t3)
    p4 = Place()
    Arc(t3, p4)
    t4 = Transition(name="Denial")
    t4.priority = 1
    Arc(p2, t4)
    Arc(p3, t4, buffer_size, is_informational=True)
    p5 = Place()
    Arc(t4, p5)
    p6 = Place(markers=2)
    Arc(t3, p6)
    Arc(p6, t3)

    model = Model([t1, t2, t3, t4],
                  [p1, p2, p3, p4, p5, p6])
    model.simulate_for_iterations(1000)

def lab8_task3(processors_count = 6):
    p1 = Place(1)
    t1 = Transition(name="Надходження типу1")
    Arc(p1, t1)
    Arc(t1, p1)
    p2 = Place()
    Arc(t1, p2)
    t2 = Transition(name="Обробка типу1")
    Arc(p2, t2)
    p3 = Place(name="Виконані типу1")
    Arc(t2, p3)

    p4 = Place(1)
    t3 = Transition(name="Надходження типу2")
    Arc(p4, t3)
    Arc(t3, p4)
    p5 = Place()
    Arc(t3, p5)
    t4 = Transition(name="Обробка типу2")
    Arc(p5, t4)
    p6 = Place(name="Виконані типу2")
    Arc(t4, p6)

    p7 = Place(1)
    t5 = Transition(name="Надходження типу3")
    Arc(p7, t5)
    Arc(t5, p7)
    p8 = Place()
    Arc(t5, p8)
    t6 = Transition(name="Обробка типу3")
    Arc(p8, t6)
    p9 = Place(name="Виконані типу3")
    Arc(t6, p9)

    p10 = Place(name="К-сть вільних процесорів", markers=processors_count)
    Arc(p10, t2, processors_count)
    Arc(t2, p10, processors_count)
    Arc(p10, t4, processors_count/3)
    Arc(t4, p10, processors_count/3)
    Arc(p10, t6, processors_count/2)
    Arc(t6, p10, processors_count/2)

    model = Model([t1, t2, t3, t4, t5, t6],
                  [p1, p2, p3, p4, p5, p6, p7, p8, p9, p10])
    model.simulate_for_iterations(100)

def verification():
    Model.verification = True
    Model.print_verification_header()
    conveer_task()
    conveer_task(det1=40)
    conveer_task(det2=10)
    conveer_task(complect_d2=80)
    conveer_task(complect_d1=5)
    conveer_task(det1=60, det2=20, complect_d1=120, complect_d2=10)


if __name__ == '__main__':
    #lab8_task1()
    lab8_task2(buffer_size=1)
    #lab8_task3()





